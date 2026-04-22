
let loggedUser = null;
let loggedUserSectorId = null;

let tasks = [];
let filteredTasks = [];
let currentPage = 1;
let editingTaskId = null;

let sectors = [];

const ITEMS_PER_PAGE = 6;

/* ================= NORMALIZAR CPF ================= */
function normalizeCPF(cpf) {
    if (!cpf) return "";
    return cpf.replace(/\D/g, "");
}

/* ================= LOGOUT ================= */
async function logout() {
    try {
        await fetch("/logout", { method: "POST" });
    } catch (e) {
        console.error("Erro ao sair:", e);
    } finally {
        window.location.href = "/login";
    }
}

/* ================= SIDEBAR ================= */
function toggleSidebar() {
    const sidebar = document.querySelector(".sidebar");
    if (sidebar) {
        sidebar.classList.toggle("open");
    }
}

/* ================= FILTRO SETORES ================= */
function filtrarSetores() {
    const input = document.getElementById("searchSetor");
    if (!input) return;

    const filter = input.value.toLowerCase();
    const items = document.querySelectorAll(".sector-item");

    items.forEach(item => {
        const text = item.textContent.toLowerCase();
        item.style.display = text.includes(filter) ? "" : "none";
    });
}

/* ================= CRIAR / ATUALIZAR ================= */
async function createTask() {

    if (!loggedUser) {
        alert("Usuário não autenticado");
        return;
    }

    const title = document.getElementById("title").value.trim();
    const description = document.getElementById("description").value.trim();
    const status = document.getElementById("status")?.value || "PENDING";

    if (!title) return;

    try {

        const url = editingTaskId 
            ? `/tasks/${editingTaskId}` 
            : "/tasks";

        const method = editingTaskId 
            ? "PUT" 
            : "POST";

        const bodyData = {
            title,
            description,
            status
        };

        // 🔥 TRATAMENTO CORRETO DE SETOR
        if (loggedUser.role === "SUPERADMIN") {

            const selectedSector = document.getElementById("sectorSelect")?.value;

            if (!selectedSector) {
                alert("Selecione um setor");
                return;
            }

            bodyData.sectorId = Number(selectedSector);

        } else {

            if (loggedUserSectorId) {
                bodyData.sectorId = loggedUserSectorId;
            }
        }

        const response = await fetch(url, {
            method: method,
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(bodyData)
        });

        if (response.ok) {
            await carregarTarefas();
            limparFormulario();
            editingTaskId = null;
        } else {
            const errorText = await response.text();
            console.error("Erro backend:", errorText);
            alert("Erro ao salvar tarefa");
        }

    } catch (error) {
        console.error("Erro:", error);
    }
}
/* ================= LIMPAR ================= */
function limparFormulario() {
    document.getElementById("title").value = "";
    document.getElementById("description").value = "";
    const statusEl = document.getElementById("status");
    if (statusEl) statusEl.value = "PENDING"; 
}

/* ================= FILTROS ================= */
function aplicarFiltros() {

    const searchRaw = document.getElementById("searchInput")?.value.toLowerCase().trim() || "";
    const searchCPF = normalizeCPF(searchRaw);

    const statusFilter = document.getElementById("filterStatus")?.value || "ALL";
    const userFilter = document.getElementById("filterUser")?.value || "ALL";

    filteredTasks = tasks.filter(task => {

        const matchUser =
            userFilter === "ALL" || (task.userName && task.userName === userFilter);

        const matchStatus =
            statusFilter === "ALL" || task.status === statusFilter;

        const title = task.title?.toLowerCase() || "";
        const description = task.description?.toLowerCase() || "";
        const taskCPF = normalizeCPF(task.cpf || "");

        const matchSearch =
            !searchRaw ||
            title.includes(searchRaw) ||
            description.includes(searchRaw) ||
            taskCPF.includes(searchCPF);

        return matchUser && matchStatus && matchSearch;
    });

    currentPage = 1;
    render();
}

/* ================= RENDER ================= */
function render() {
    renderTasks();
    renderPagination();
}

/* ================= TAREFAS ================= */
function renderTasks() {

    const list = document.getElementById("taskList");
    if (!list) return;

    list.innerHTML = "";

    if (!filteredTasks || filteredTasks.length === 0) {
        list.innerHTML = "<p style='text-align:center;'>Nenhuma tarefa encontrada.</p>";
        return;
    }

    const start = (currentPage - 1) * ITEMS_PER_PAGE;
    const end = start + ITEMS_PER_PAGE;

    const pageItems = filteredTasks.slice(start, end);

    pageItems.forEach(task => {

        const li = document.createElement("li");
        li.className = "task-card";

        li.innerHTML = `
            <div class="task-info">
                <strong>${task.title}</strong>
                <p>${task.description || ""}</p>

                <div class="task-meta">
                    <div class="task-user-badge">
                        👤 ${task.userName?.toUpperCase() || "—"}
                    </div>

                    <div class="task-date">
                        📅 ${task.createdAt ? formatDate(task.createdAt) : ""}
                    </div>
                </div>
            </div>

            <div class="status-badge ${getStatusClass(task.status)}">
                ${getStatusLabel(task.status)}
            </div>

            <div class="task-actions">
                <button onclick="editTask(${task.id})">✏️</button>
                <button onclick="deleteTask(${task.id})">❌</button>
            </div>
        `;

        list.appendChild(li);
    });
}

/* ================= DELETE ================= */
async function deleteTask(id) {

    if (!confirm("Deseja excluir esta tarefa?")) return;

    try {
        const response = await fetch(`/tasks/${id}`, {
            method: "DELETE"
        });

        if (response.ok) {
            await carregarTarefas();
        } else {
            alert("Erro ao excluir");
        }

    } catch (e) {
        console.error("Erro ao deletar:", e);
    }
}

/* ================= EDITAR ================= */
function editTask(id) {

    const task = tasks.find(t => t.id === id);
    if (!task) return;

    document.getElementById("title").value = task.title;
    document.getElementById("description").value = task.description || "";
    document.getElementById("status").value = task.status;

    editingTaskId = id;

    document.getElementById("submitBtn").innerText = "Atualizar";
}

/* ================= PAGINAÇÃO ================= */
function renderPagination() {

    const totalPages = Math.ceil(filteredTasks.length / ITEMS_PER_PAGE);

    if (currentPage > totalPages) {
        currentPage = totalPages || 1;
    }

    const containers = [
        document.getElementById("paginationTop"),
        document.getElementById("paginationBottom")
    ];

    containers.forEach(container => {

        if (!container) return;

        container.innerHTML = "";

        if (totalPages <= 1) return;

        for (let i = 1; i <= totalPages; i++) {
            const btn = document.createElement("button");
            btn.textContent = i;

            if (i === currentPage) btn.classList.add("active");

            btn.onclick = () => {
                currentPage = i;
                render();
            };

            container.appendChild(btn);
        }
    });
}

/* ================= STATUS ================= */
function getStatusClass(status) {
    if (status === "PENDING") return "status-pending";
    if (status === "IN_PROGRESS") return "status-in-progress";
    return "status-completed";
}

function getStatusLabel(status) {
    if (status === "PENDING") return "Pendente";
    if (status === "IN_PROGRESS") return "Em andamento";
    return "Concluída";
}

/* ================= DATA ================= */
function formatDate(dateString) {
    const date = new Date(dateString);
    return isNaN(date) ? "" : date.toLocaleDateString("pt-BR");
}

/* ================= USER FILTER ================= */
function updateUserFilterOptions() {

    const select = document.getElementById("filterUser");
    if (!select) return;

    const users = [...new Set(tasks.map(t => t.userName))];

    select.innerHTML = `<option value="ALL">Todos</option>`;

    users.forEach(user => {
        if (!user) return;

        const option = document.createElement("option");
        option.value = user;
        option.textContent = user;
        select.appendChild(option);
    });
}

/* ================= USUÁRIO LOGADO ================= */
async function carregarUsuarioLogado() {
    try {
        const response = await fetch("/auth/me");

        if (!response.ok) throw new Error("Não autenticado");

        const data = await response.json();

        loggedUser = data;
        loggedUserSectorId = data.sectorId || null;

        document.getElementById("loggedUserName").innerText = data.username;

        // 🔥 SUPERADMIN vê e carrega setores
        if (loggedUser.role === "SUPERADMIN") {
            const sectorSelect = document.getElementById("sectorSelect");

            if (sectorSelect) {
                sectorSelect.style.display = "inline-block";
                await loadSectorFilter(); // 🔥 CARREGA OS SETORES
            }
        }

    } catch (e) {
        console.error("Erro ao pegar usuário:", e);
    }
}

/* ================= CARREGAR TAREFAS ================= */
async function carregarTarefas() {

    try {
        const response = await fetch("/tasks?page=0&size=50");

        if (!response.ok) throw new Error("Erro ao buscar tarefas");

        const data = await response.json();

        tasks = Array.isArray(data) ? data : (data.content || []);

        tasks.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));

        filteredTasks = [...tasks];

        updateUserFilterOptions();
        aplicarFiltros();

    } catch (e) {
        console.error(e);
        tasks = [];
        filteredTasks = [];
        render();
    }
}

/* ================= CARREGAR SETORES ================= */
async function carregarSetores() {
    try {
        const response = await fetch("/sectors/with-users"); // endpoint sectores corretos

        if (!response.ok) throw new Error("Erro ao buscar setores");

        const data = await response.json();

        sectors = Array.isArray(data) ? data : (data.content || []);

        renderSetores();

    } catch (e) {
        console.error("Erro ao carregar setores:", e);
    }
}
/* ============== SETORES RENDERIZAR ====== */

function renderSetores() {

    const list = document.getElementById("lista-setores");
    if (!list) return;

    list.innerHTML = "";

    sectors.forEach(sector => {

        const li = document.createElement("li");
        li.className = "sector-item";

        // HEADER DO SETOR
        const header = document.createElement("div");
        header.className = "sector-header";
        header.innerHTML = `
            <span class="sector-icon">📁</span>
            <span class="sector-name">${sector.name}</span>
        `;

        // LISTA DE USUÁRIOS (INICIALMENTE ESCONDIDA)
        const usersList = document.createElement("ul");
        usersList.className = "sector-users";
        usersList.style.display = "none";

        // 🔥 CORREÇÃO AQUI (compatível com qualquer nome vindo do backend)
        const users =
            sector.users ||
            sector.userList ||
            sector.usersList ||
            sector.usuarios ||
            [];

        if (users.length > 0) {
            users.forEach(user => {
                const userLi = document.createElement("li");
                userLi.className = "user-item";
                userLi.innerText = `👤 ${user.name || user.username}`;
                usersList.appendChild(userLi);
            });
        } else {
            const empty = document.createElement("li");
            empty.innerText = "Sem usuários";
            usersList.appendChild(empty);
        }

        // TOGGLE (EXPANDIR / RECOLHER)
        header.onclick = () => {
            usersList.style.display =
                usersList.style.display === "none" ? "block" : "none";
        };

        li.appendChild(header);
        li.appendChild(usersList);
        list.appendChild(li);
    });
}
/* ================= INIT ================= */
document.addEventListener("DOMContentLoaded", () => {

    carregarUsuarioLogado();
    carregarTarefas();
    carregarSetores();

    document.getElementById("filterUser")
        ?.addEventListener("change", aplicarFiltros);
});