document.addEventListener('DOMContentLoaded', () => {
  // 1. Navigation Logic
  const navLinks = document.querySelectorAll('.nav-link');
  const sections = document.querySelectorAll('main > section');

  navLinks.forEach(link => {
    link.addEventListener('click', (e) => {
      e.preventDefault();
      const targetId = link.getAttribute('data-target');

      // Remove active classes
      navLinks.forEach(l => l.classList.remove('active'));
      sections.forEach(s => s.classList.remove('active-section', 'hidden'));

      // Add active to clicked nav and target section
      link.classList.add('active');
      sections.forEach(s => {
        if (s.id === targetId) {
          s.classList.add('active-section');
        } else {
          s.classList.add('hidden');
        }
      });
    });
  });

  // 2. Auth Logic
  const apiUrlInput = document.getElementById('api-url-input');
  const apiKeyInput = document.getElementById('api-key-input');
  const btnSaveKey = document.getElementById('btn-save-key');
  const btnClearKey = document.getElementById('btn-clear-key');
  const authMessage = document.getElementById('auth-message');
  const authStatus = document.getElementById('auth-status');

  // Default URL logic
  function getDefaultUrl() {
    const defaultPort = "8765";
    const hostname = window.location.hostname || "127.0.0.1";
    const protocol = window.location.protocol === "file:" ? "http:" : window.location.protocol;
    return `${protocol}//${hostname}:${defaultPort}`;
  }

  // Load existing settings
  const existingUrl = sessionStorage.getItem('mclang_api_url') || getDefaultUrl();
  apiUrlInput.value = existingUrl;

  const existingKey = sessionStorage.getItem('mclang_api_key');
  if (existingKey) {
    apiKeyInput.value = existingKey;
    setAuthBadge(true);
  }

  btnSaveKey.addEventListener('click', () => {
    let url = apiUrlInput.value.trim();
    const key = apiKeyInput.value.trim();

    if (!url) url = getDefaultUrl();
    // Remove trailing slash
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
    showToast('✅ 設定已成功保存至瀏覽器！', '#10b981');
  });

  btnClearKey.addEventListener('click', () => {
    sessionStorage.removeItem('mclang_api_key');
    sessionStorage.removeItem('mclang_api_url');
    apiKeyInput.value = '';
    apiUrlInput.value = getDefaultUrl();
    showToast('🗑️ 快取設定已清除', '#94a3b8');
    setAuthBadge(false);
  });

  function showToast(msg, color) {
    authMessage.textContent = msg;
    authMessage.style.color = color;
    authMessage.classList.add('show');
    setTimeout(() => authMessage.classList.remove('show'), 3000);
  }

  function setAuthBadge(isAuth) {
    if (isAuth) {
      authStatus.textContent = 'Authorized';
      authStatus.classList.remove('unauthorized');
      authStatus.classList.add('authorized');
    } else {
      authStatus.textContent = 'Unauthenticated';
      authStatus.classList.remove('authorized');
      authStatus.classList.add('unauthorized');
    }
  }
});

// 3. API Fetch Wrappers
async function fetchApi(url, method = 'GET') {
  const viewer = document.getElementById('api-response-viewer');
  const statusDisp = document.getElementById('response-status');
  viewer.textContent = 'Loading...';
  statusDisp.textContent = '';

  // Automatically attach token from SessionStorage
  const token = sessionStorage.getItem('mclang_api_key');
  const headers = {};
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  try {
    const response = await fetch(url.toString(), {
      method: method,
      headers: headers
    });

    const status = response.status;
    const statusText = response.statusText;
    statusDisp.textContent = `${status} ${statusText}`;
    statusDisp.className = `status-code ${status >= 200 && status < 300 ? 'success' : 'error'}`;

    let data;
    const contentType = response.headers.get("content-type");
    if (contentType && contentType.indexOf("application/json") !== -1) {
      data = await response.json();
      viewer.innerHTML = syntaxHighlight(JSON.stringify(data, null, 2));
    } else {
      data = await response.text();
      viewer.textContent = data;
    }

    // Handle 401 specifically
    if (status === 401) {
      alert("⚠️ 存取被拒絕 (401 Unauthorized)。請檢查您的 API Key 是否正確。");
      document.querySelector('.nav-link[data-target="section-auth"]').click();
    }

  } catch (err) {
    viewer.textContent = 'Network Error: ' + err.message;
    statusDisp.textContent = 'ERROR';
    statusDisp.className = 'status-code error';
  }
}

// Format JSON
function syntaxHighlight(json) {
  json = json.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
  return json.replace(/("(\\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)/g, function (match) {
    let cls = 'json-number';
    if (/^"/.test(match)) {
      if (/:$/.test(match)) {
        cls = 'json-key';
      } else {
        cls = 'json-string';
      }
    } else if (/true|false/.test(match)) {
      cls = 'json-boolean';
    } else if (/null/.test(match)) {
      cls = 'json-null';
    }
    return '<span class="' + cls + '">' + match + '</span>';
  });
}

// 4. Endpoint Specific Handlers
function getApiBaseUrl() {
  const defaultPort = "8765";
  const hostname = window.location.hostname || "127.0.0.1";
  const protocol = window.location.protocol === "file:" ? "http:" : window.location.protocol;
  const defaultUrl = `${protocol}//${hostname}:${defaultPort}`;
  return sessionStorage.getItem('mclang_api_url') || defaultUrl;
}

window.testApi = function (path, method) {
  fetchApi(`${getApiBaseUrl()}${path}`, method);
};

window.testLanguageDetail = function () {
  const lang = document.getElementById('param-lang').value || 'zh_tw';
  const version = document.getElementById('param-version').value;
  const limit = document.getElementById('param-limit').value || '50';

  const url = new URL(`${getApiBaseUrl()}/api/languages/${lang}`);

  if (version) url.searchParams.append('version', version);
  if (limit) url.searchParams.append('limit', limit);

  fetchApi(url, 'GET');
};

window.testTranslate = function () {
  const lang = document.getElementById('t-lang').value || 'zh_tw';
  const key = document.getElementById('t-key').value;

  if (!key) {
    alert("請輸入需翻譯的 key！");
    return;
  }

  const url = new URL(`${getApiBaseUrl()}/api/translate`);
  url.searchParams.append('lang', lang);
  url.searchParams.append('key', key);

  fetchApi(url, 'GET');
};
