// ============================================================
//  MCLang Web Portal — app.js
//  i18n · Auth · Navigation · API Playground
// ============================================================

// ── i18n ─────────────────────────────────────────────────────
let currentLang = localStorage.getItem('mclang_lang') || 'en-US';
let translations = {};

async function loadLanguage(lang) {
  try {
    const response = await fetch(`lang/${lang}.json`);
    if (!response.ok) throw new Error('Language file not found');
    translations = await response.json();
    currentLang = lang;
    localStorage.setItem('mclang_lang', lang);
    applyTranslations();
  } catch (err) {
    console.error('Failed to load language:', err);
  }
}

function applyTranslations() {
  document.querySelectorAll('[data-i18n]').forEach(el => {
    const key = el.getAttribute('data-i18n');
    if (translations[key]) el.innerHTML = translations[key];
  });

  document.querySelectorAll('[data-i18n-placeholder]').forEach(el => {
    const key = el.getAttribute('data-i18n-placeholder');
    if (translations[key]) el.placeholder = translations[key];
  });
}

/** Get a translation string with optional fallback */
window.t = function (key, fallback = '') {
  return translations[key] || fallback;
};

// ── DOM Ready ─────────────────────────────────────────────────
document.addEventListener('DOMContentLoaded', () => {

  // ── Language selector ──────────────────────────────────────
  const langSelector = document.getElementById('lang-selector');
  if (langSelector) {
    langSelector.value = currentLang;
    langSelector.addEventListener('change', e => loadLanguage(e.target.value));
  }
  loadLanguage(currentLang);

  // ── Navigation ─────────────────────────────────────────────
  // (Navigation is now handled cleanly by `navTo(this)` in the index.html)


  // ── Auth Logic ─────────────────────────────────────────────
  const apiUrlInput  = document.getElementById('api-url-input');
  const apiKeyInput  = document.getElementById('api-key-input');
  const btnSaveKey   = document.getElementById('btn-save-key');
  const btnClearKey  = document.getElementById('btn-clear-key');
  const authMessage  = document.getElementById('auth-message');
  const authStatus   = document.getElementById('auth-status');

  // Derive a sensible default base URL
  function getDefaultUrl() {
    const defaultPort = '8765';
    const hostname    = window.location.hostname || '127.0.0.1';
    const protocol    = window.location.protocol === 'file:' ? 'http:' : window.location.protocol;
    return `${protocol}//${hostname}:${defaultPort}`;
  }

  // Restore persisted settings
  apiUrlInput.value = sessionStorage.getItem('mclang_api_url') || getDefaultUrl();

  const existingKey = sessionStorage.getItem('mclang_api_key');
  if (existingKey) {
    apiKeyInput.value = existingKey;
    setAuthBadge(true);
  }

  // Save
  btnSaveKey.addEventListener('click', () => {
    let url = apiUrlInput.value.trim();
    const key = apiKeyInput.value.trim();

    if (!url) url = getDefaultUrl();
    if (url.endsWith('/')) url = url.slice(0, -1);

    sessionStorage.setItem('mclang_api_url', url);
    apiUrlInput.value = url;

    if (key) {
      sessionStorage.setItem('mclang_api_key', key);
      setAuthBadge(true);
    } else {
      sessionStorage.removeItem('mclang_api_key');
      setAuthBadge(false);
    }

    showToast(window.t('toast.save', '✅ 設定已成功保存至瀏覽器！'), '#00c47a');
  });

  // Clear
  btnClearKey.addEventListener('click', () => {
    sessionStorage.removeItem('mclang_api_key');
    sessionStorage.removeItem('mclang_api_url');
    apiKeyInput.value  = '';
    apiUrlInput.value  = getDefaultUrl();
    showToast(window.t('toast.clear', '🗑️ 快取設定已清除'), '#7a9688');
    setAuthBadge(false);
  });

  // Toast helper
  function showToast(msg, color) {
    authMessage.textContent     = msg;
    authMessage.style.color     = color;
    authMessage.style.background = color + '18';
    authMessage.style.border    = `1px solid ${color}44`;
    authMessage.classList.add('show');
    setTimeout(() => authMessage.classList.remove('show'), 3000);
  }

  // Auth badge helper (updates both sidebar badge + mobile dot)
  function setAuthBadge(isAuth) {
    const mobileStatus = document.getElementById('auth-status-mobile');

    if (isAuth) {
      authStatus.textContent = window.t('status.auth', 'Authorized');
      authStatus.setAttribute('data-i18n', 'status.auth');
      authStatus.className = 'status-badge authorized';
      if (mobileStatus) mobileStatus.className = 'status-badge-sm authorized';
    } else {
      authStatus.textContent = window.t('status.unauth', 'Unauthenticated');
      authStatus.setAttribute('data-i18n', 'status.unauth');
      authStatus.className = 'status-badge unauthorized';
      if (mobileStatus) mobileStatus.className = 'status-badge-sm unauthorized';
    }
  }

  // Keep mobile badge in sync on load
  const mobileStatus = document.getElementById('auth-status-mobile');
  if (mobileStatus) {
    mobileStatus.className = existingKey
      ? 'status-badge-sm authorized'
      : 'status-badge-sm unauthorized';
  }
});

// ── API Fetch ─────────────────────────────────────────────────
async function fetchApi(url, method = 'GET') {
  const viewer    = document.getElementById('api-response-viewer');
  const statusEl  = document.getElementById('response-status');

  viewer.textContent   = window.t('console.loading', 'Loading...');
  statusEl.textContent = '';
  statusEl.className   = 'response-status';

  const token   = sessionStorage.getItem('mclang_api_key');
  const headers = {};
  if (token) headers['Authorization'] = `Bearer ${token}`;

  try {
    const response   = await fetch(url.toString(), { method, headers });
    const status     = response.status;
    const statusText = response.statusText;

    // Update status badge
    statusEl.textContent = `${status} ${statusText}`;
    statusEl.className   = `response-status ${status >= 200 && status < 300 ? 'success' : 'error'}`;

    // Parse + render body
    const contentType = response.headers.get('content-type') || '';
    if (contentType.includes('application/json')) {
      const data = await response.json();
      viewer.innerHTML = syntaxHighlight(JSON.stringify(data, null, 2));
    } else {
      viewer.textContent = await response.text();
    }

    // Redirect to auth section on 401
    if (status === 401) {
      alert(window.t('alert.unauth', '⚠️ 存取被拒絕 (401 Unauthorized)。請檢查您的 API Key 是否正確。'));
      document.querySelector('.nav-link[data-target="section-auth"]').click();
    }

  } catch (err) {
    viewer.textContent   = window.t('console.error', 'Network Error: ') + err.message;
    statusEl.textContent = 'ERROR';
    statusEl.className   = 'response-status error';
  }
}

// ── JSON Syntax Highlighter ───────────────────────────────────
function syntaxHighlight(json) {
  json = json
    .replace(/&/g,  '&amp;')
    .replace(/</g,  '&lt;')
    .replace(/>/g,  '&gt;');

  return json.replace(
    /(\"(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*\"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)/g,
    match => {
      let cls = 'json-number';
      if (/^"/.test(match)) {
        cls = /:$/.test(match) ? 'json-key' : 'json-string';
      } else if (/true|false/.test(match)) {
        cls = 'json-boolean';
      } else if (/null/.test(match)) {
        cls = 'json-null';
      }
      return `<span class="${cls}">${match}</span>`;
    }
  );
}

// ── Base URL Helper ───────────────────────────────────────────
function getApiBaseUrl() {
  const defaultPort = '8765';
  const hostname    = window.location.hostname || '127.0.0.1';
  const protocol    = window.location.protocol === 'file:' ? 'http:' : window.location.protocol;
  return sessionStorage.getItem('mclang_api_url') || `${protocol}//${hostname}:${defaultPort}`;
}

// ── Public Endpoint Handlers ──────────────────────────────────
window.testApi = function (path, method) {
  fetchApi(`${getApiBaseUrl()}${path}`, method);
};

window.testLanguageDetail = function () {
  const lang    = document.getElementById('param-lang').value    || 'zh_tw';
  const version = document.getElementById('param-version').value;
  const limit   = document.getElementById('param-limit').value   || '50';

  const url = new URL(`${getApiBaseUrl()}/api/languages/${lang}`);
  if (version) url.searchParams.append('version', version);
  if (limit)   url.searchParams.append('limit',   limit);

  fetchApi(url, 'GET');
};

window.testTranslate = function () {
  const lang = document.getElementById('t-lang').value;
  const key  = document.getElementById('t-key').value;

  if (!key) {
    alert(window.t('alert.nokey', '請輸入需翻譯的 key！'));
    return;
  }

  const url = new URL(`${getApiBaseUrl()}/api/translate`);
  url.searchParams.append('lang', lang || 'zh_tw');
  url.searchParams.append('key',  key);

  fetchApi(url, 'GET');
};
