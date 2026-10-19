const clause = document.querySelector("#clause");
const status = document.querySelector("#status");
const diagnostics = document.querySelector("#diagnostics");
const version = document.querySelector("#version");
const highlight = document.querySelector("#highlight");
const history = document.querySelector("#history");
let timer;

async function validate() {
  const response = await fetch("/api/validate", {
    method: "POST",
    headers: {"Content-Type": "application/json"},
    body: JSON.stringify({text: clause.value})
  });
  const result = await response.json();
  status.textContent = result.message;
  status.className = `status ${result.accepted ? "accepted" : "rejected"}`;
  version.textContent = `Grammar v${result.grammarVersion}`;
  diagnostics.replaceChildren(...result.diagnostics.map(item => {
    const li = document.createElement("li"); li.textContent = item; return li;
  }));
  highlight.innerHTML = result.accepted ? "✓ All tokens satisfy the current grammar."
    : "⚠ <span class=\"illegal\">Clause does not match the current grammar.</span>";
}

async function loadHistory() {
  const response = await fetch("/api/grammar/versions");
  const items = await response.json();
  history.replaceChildren(...items.map(item => {
    const li = document.createElement("li");
    li.textContent = `v${item.version} — ${item.reason}`;
    return li;
  }));
}

clause.addEventListener("input", () => {
  clearTimeout(timer);
  timer = setTimeout(validate, 250);
});
validate();
loadHistory();
