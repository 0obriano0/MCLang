function apiBaseUrl() {
  return document.getElementById('apiBaseUrl').value.trim().replace(/\/$/, '');
}

async function getJson(path, options) {
  const res = await fetch(apiBaseUrl() + path, options);
  return res.json();
}

async function loadDocs() {
  document.getElementById('docs').textContent = JSON.stringify(await getJson('/api/docs'), null, 2);
}

async function loadLanguages() {
  document.getElementById('langs').textContent = JSON.stringify(await getJson('/api/languages'), null, 2);
}

async function loadLangData() {
  const lang = document.getElementById('lang').value.trim();
  const version = document.getElementById('version').value.trim();
  const prefix = document.getElementById('prefix').value.trim();
  const q = new URLSearchParams({ limit: '80', offset: '0' });
  if (version) q.set('version', version);
  if (prefix) q.set('prefix', prefix);
  document.getElementById('langs').textContent = JSON.stringify(
    await getJson('/api/languages/' + encodeURIComponent(lang) + '?' + q.toString()),
    null,
    2
  );
}

async function translateGet() {
  const lang = document.getElementById('tlang').value.trim();
  const key = document.getElementById('tkey').value.trim();
  const q = new URLSearchParams({ lang, key });
  document.getElementById('translate').textContent = JSON.stringify(
    await getJson('/api/translate?' + q.toString()),
    null,
    2
  );
}

async function translatePost() {
  const lang = document.getElementById('tlang').value.trim();
  const key = document.getElementById('tkey').value.trim();
  document.getElementById('translate').textContent = JSON.stringify(
    await getJson('/api/translate', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ lang, key })
    }),
    null,
    2
  );
}
