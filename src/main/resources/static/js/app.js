let loggedUser = null;
let loggedUserSectorId = null;

let tasks = [];
let filteredTasks = [];
let currentPage = 1;
let editingTaskId = null;

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

        item.style.display = text.includes(filter)
            ? ""
            : "none";
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
	
	if (description.length > 1000) {
	    alert("Descrição deve ter no máximo 1000 caracteres.");
	    return;
	}
	
    const status =
        document.getElementById("status")?.value || "PENDING";

    const priority =
        document.getElementById("priority")?.value || "MEDIUM";

    if (!title) return;

	let selectedSector = null;
	
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
            status,
            priority
        };

        // ================= SUPERADMIN =================
		if (loggedUser.role === "SUPERADMIN") {

		    selectedSector =
		        document.getElementById("taskSectorSelect")?.value;

		    if (
		        !selectedSector ||
		        selectedSector === "" ||
		        selectedSector === "null"
		    ) {
		        alert("Selecione um setor");
		        return;
		    }

		    bodyData.sectorId =
		        parseInt(selectedSector);

		} else {

            if (loggedUserSectorId) {
                bodyData.sectorId = loggedUserSectorId;
            }
        }
        
		/* remover linha abaixo apos erro parar */
		console.log("Usuário logado:", loggedUser);

		console.log(
		    "Role usuário:",
		    loggedUser?.role
		);

		console.log(
		    "Setor selecionado:",
		    selectedSector
		);

		console.log(
		    "Body enviada:",
		    bodyData
		);
		/* até aqui */
		
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

            document.getElementById("submitBtn").innerText = "Criar";

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

    if (statusEl) {
        statusEl.value = "PENDING";
    }

    const priorityEl = document.getElementById("priority");

    if (priorityEl) {
        priorityEl.value = "MEDIUM";
    }
}

/* ================= FILTROS ================= */
function aplicarFiltros() {

    const searchRaw =
        document.getElementById("searchInput")
            ?.value
            .toLowerCase()
            .trim() || "";

    const searchCPF = normalizeCPF(searchRaw);

    const statusFilter =
        document.getElementById("filterStatus")?.value || "ALL";

    const userFilter =
        document.getElementById("filterUser")?.value || "ALL";

    filteredTasks = tasks.filter(task => {

        const matchUser =
            userFilter === "ALL" ||
            (
                task.assignedToName &&
                task.assignedToName === userFilter
            );

        const matchStatus =
            statusFilter === "ALL" ||
            task.status === statusFilter;

        const title =
            task.title?.toLowerCase() || "";

        const description =
            task.description?.toLowerCase() || "";

        const taskCPF =
            normalizeCPF(task.cpf || "");

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

/* ================= DASHBOARD ================= */
function updateDashboard() {

    document.getElementById("totalTasks").innerText =
        tasks.length;

    document.getElementById("pendingTasks").innerText =
        tasks.filter(t => t.status === "PENDING").length;

    document.getElementById("progressTasks").innerText =
        tasks.filter(t => t.status === "IN_PROGRESS").length;

    document.getElementById("doneTasks").innerText =
        tasks.filter(t => t.status === "COMPLETED").length;
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

        list.innerHTML =
            "<p style='text-align:center;'>Nenhuma tarefa encontrada.</p>";

        return;
    }

    const start = (currentPage - 1) * ITEMS_PER_PAGE;

    const end = start + ITEMS_PER_PAGE;

    const pageItems = filteredTasks.slice(start, end);

    pageItems.forEach(task => {

		const li = document.createElement("li");

		li.id = `task-${task.id}`;

		li.className = `task-card ${getPriorityClass(task.priority)}`;

        li.innerHTML = `

            <div class="task-info">

                <strong>${task.title}</strong>

				<p class="task-description">
				    ${task.description || ""}
				</p>

                <div class="task-meta">

                    <div class="task-user-badge">
                        👤 Responsável:
                        ${task.assignedToName?.toUpperCase() || "—"}
                    </div>

                    <div class="task-created-by">
                        ✍️ Criado por:
                        ${task.createdByName || "—"}
                    </div>

                    <div class="task-priority">
                        🚨 Prioridade:
                        ${getPriorityLabel(task.priority)}
                    </div>

                    <div class="task-date">
                        📅
                        ${task.createdAt
                            ? formatDate(task.createdAt)
                            : ""}
                    </div>

                </div>
				
				
            </div>

            <div class="status-badge ${getStatusClass(task.status)}">
                ${getStatusLabel(task.status)}
            </div>

            <div class="task-actions">
			
			    <button onclick="openTaskModal(${task.id})">👁️</button>
			
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

    document.getElementById("title").value =
        task.title;

    document.getElementById("description").value =
        task.description || "";

    document.getElementById("status").value =
        task.status;

    document.getElementById("priority").value =
        task.priority;

    editingTaskId = id;

    document.getElementById("submitBtn").innerText =
        "Atualizar";
}

/* ================= PAGINAÇÃO ================= */
function renderPagination() {

    const totalPages =
        Math.ceil(filteredTasks.length / ITEMS_PER_PAGE);

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

            if (i === currentPage) {
                btn.classList.add("active");
            }

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

    if (status === "PENDING") {
        return "status-pending";
    }

    if (status === "IN_PROGRESS") {
        return "status-in-progress";
    }

    return "status-completed";
}

function getStatusLabel(status) {

    if (status === "PENDING") {
        return "Pendente";
    }

    if (status === "IN_PROGRESS") {
        return "Em andamento";
    }

    return "Concluída";
}

/* ================= PRIORIDADE ================= */
function getPriorityLabel(priority) {

    if (priority === "LOW") {
        return "Pequena";
    }

    if (priority === "MEDIUM") {
        return "Média";
    }

    if (priority === "HIGH") {
        return "Alta";
    }

    if (priority === "URGENT") {
        return "Urgente";
    }

    return "Não definida";
}

/* ================= PRIORITY CLASS ================= */
function getPriorityClass(priority) {

    if (priority === "HIGH") {
        return "priority-high";
    }

    if (priority === "URGENT") {
        return "priority-urgent";
    }

    return "priority-normal";
}

/* ================= DATA ================= */
function formatDate(dateString) {

    const date = new Date(dateString);

    return isNaN(date)
        ? ""
        : date.toLocaleDateString("pt-BR");
}

/* ================= USER FILTER ================= */
function updateUserFilterOptions() {

    const select =
        document.getElementById("filterUser");

    if (!select) return;

    const users = [
        ...new Set(tasks.map(t => t.assignedToName))
    ];

    select.innerHTML =
        `<option value="ALL">Todos</option>`;

    users.forEach(user => {

        if (!user) return;

        const option = document.createElement("option");

        option.value = user;

        option.textContent = user;

        select.appendChild(option);
    });
}


/* ================= CARREGAR SELECT DE SETORES ================= */

async function loadSectorFilter() {

    try {

        const response =
            await fetch("/sectors");

        if (!response.ok) {
            throw new Error("Erro ao buscar setores");
        }

        const sectors = await response.json();

        const select =
            document.getElementById("taskSectorSelect");

        if (!select) return;

        select.innerHTML =
            `<option value="">Selecione o setor</option>`;

        sectors.forEach(sector => {

            const option =
                document.createElement("option");

            option.value = sector.id;

            option.textContent = sector.name;

            select.appendChild(option);

        });

    } catch (e) {

        console.error(
            "Erro ao carregar setores:",
            e
        );
    }
}


/* ================= USUÁRIO LOGADO ================= */
async function carregarUsuarioLogado() {

    try {

        const response = await fetch("/auth/me");

        if (!response.ok) {
            throw new Error("Não autenticado");
        }

        const data = await response.json();

        loggedUser = data;

        loggedUserSectorId = data.sectorId || null;

        document.getElementById("loggedUserName").innerText =
            data.username;

        // ================= SUPERADMIN =================
        if (loggedUser.role === "SUPERADMIN") {

            const sectorSelect =
                document.getElementById("taskSectorSelect");

            if (sectorSelect) {

                sectorSelect.style.display =
                    "inline-block";

                await loadSectorFilter();
            }
        }

    } catch (e) {

        console.error("Erro ao pegar usuário:", e);
    }
}

/* ================= CARREGAR TAREFAS ================= */
async function carregarTarefas() {

    try {

        const response =
            await fetch("/tasks?page=0&size=50");

        if (!response.ok) {
            throw new Error("Erro ao buscar tarefas");
        }

        const data = await response.json();

        tasks = Array.isArray(data)
            ? data
            : (data.content || []);

        tasks.sort(
            (a, b) =>
                new Date(b.createdAt) -
                new Date(a.createdAt)
        );

		filteredTasks = [...tasks];

		updateDashboard();

		updateUserFilterOptions();

		aplicarFiltros();

    } catch (e) {

        console.error(e);

        tasks = [];

        filteredTasks = [];

        render();
    }
}


/* ================= ATUALIZAR UMA TAREFA ================= */

async function refreshTask(taskId) {

    try {

        const response =
            await fetch(`/tasks/${taskId}`);

        if (!response.ok) {
            throw new Error(
                "Erro ao atualizar tarefa."
            );
        }

        const updatedTask =
            await response.json();

        const index =
            tasks.findIndex(
                t => t.id === taskId
            );

        if (index >= 0) {

            tasks[index] =
                updatedTask;

        } else {

            tasks.push(
                updatedTask
            );
        }

        aplicarFiltros();

        return updatedTask;

    } catch (e) {

        console.error(e);

        return null;
    }
}

/* ================= INIT ================= */
document.addEventListener("DOMContentLoaded", () => {

    carregarUsuarioLogado();

    // ================= TELA DE TAREFAS =================
    if (document.getElementById("taskList")) {

        carregarTarefas();

        document.getElementById("filterUser")
            ?.addEventListener("change", aplicarFiltros);
    }
});


/* ================= RENDER MODAL ================= */

function renderTaskModal(task) {

    const overlay =
        document.getElementById("taskModalOverlay");

    const content =
        document.getElementById("taskModalContent");

    let historyHtml =
        "<p>Nenhum histórico.</p>";

    if (task.history && task.history.length > 0) {

        historyHtml = task.history.map((item, index) => `

            <div class="history-item">

                <div class="history-header">

                    <div class="history-user">
                        👤 ${item.userName}
                    </div>

                    <div class="history-date">
                        ${formatDate(item.createdAt)}
                    </div>

                </div>

                <div class="history-action">
                    ${item.action}
                </div>

                ${
                    item.attachments &&
                    item.attachments.length > 0
                    ? `

                    <div class="history-attachments">

                        <h4>Anexos</h4>

                        ${item.attachments.map(att => `

                            <div class="attachment-item">

                                <a
                                    href="/attachments/download/${att.id}"
                                    target="_blank"
                                >
                                    📎 ${att.originalFileName}
                                </a>

                                <span>
                                    (${(att.fileSize / 1024).toFixed(1)} KB)
                                </span>

                            </div>

                        `).join("")}

                    </div>

                    `
                    : ""
                }

                ${
                    (
                        item.oldTitle ||
                        item.oldDescription
                    ) &&
                    (
                        item.newTitle ||
                        item.newDescription
                    )
                    ? `

                    <button
                        class="history-toggle-btn"
                        onclick="toggleHistoryDetails(${index})"
                    >
                        Ver alteração
                    </button>

                    <div
                        id="history-details-${index}"
                        class="history-details"
                    >

                        ${
                            item.oldTitle
                            ? `
                            <div class="history-description-box old">
                                <h4>Título anterior</h4>
                                <p>${item.oldTitle}</p>
                            </div>
                            `
                            : ""
                        }

                        ${
                            item.newTitle
                            ? `
                            <div class="history-description-box new">
                                <h4>Novo título</h4>
                                <p>${item.newTitle}</p>
                            </div>
                            `
                            : ""
                        }

                        ${
                            item.oldDescription
                            ? `
                            <div class="history-description-box old">
                                <h4>Descrição anterior</h4>
                                <p>${item.oldDescription}</p>
                            </div>
                            `
                            : ""
                        }

                        ${
                            item.newDescription
                            ? `
                            <div class="history-description-box new">
                                <h4>Nova descrição</h4>
                                <p>${item.newDescription}</p>
                            </div>
                            `
                            : ""
                        }

                    </div>

                    `
                    : ""
                }

            </div>

        `).join("");
    }

    content.innerHTML = `

        <h2>${task.title}</h2>

        <hr>

        <p>
            <strong>Status:</strong>
            ${getStatusLabel(task.status)}
        </p>

        <p>
            <strong>Prioridade:</strong>
            ${getPriorityLabel(task.priority)}
        </p>

        <p>
            <strong>Criado por:</strong>
            ${task.createdByName || "-"}
        </p>

        <p>
            <strong>Responsável:</strong>
            ${task.assignedToName || "-"}
        </p>

        <hr>

        <h3>Descrição</h3>

        <p>
            ${task.description || "Sem descrição"}
        </p>

        <hr>

        <h3>Histórico</h3>

        <div class="task-history">
            ${historyHtml}
        </div>

        <hr>

        <h3>Anexar arquivo</h3>

        <input
            type="file"
            id="taskAttachmentFile"
        >

        <br><br>

        <button
            onclick="uploadAttachment(${task.id})"
        >
            Enviar arquivo
        </button>

    `;

    overlay.style.display = "flex";
}

/* ================= MODAL TAREFA ================= */

async function openTaskModal(taskId) {

    const task =
        tasks.find(t => t.id === taskId);

    if (!task) return;

    renderTaskModal(task);
}

/* ================= FECHAR MODAL ================= */

function closeTaskModal() {

    const overlay =
        document.getElementById("taskModalOverlay");

    overlay.style.display = "none";
}


/* ================= TOGGLE HISTÓRICO ================= */

function toggleHistoryDetails(index) {

    const el =
        document.getElementById(
            `history-details-${index}`
        );

    if (!el) return;

    el.classList.toggle("open");
}

/* ================= EVENTOS MODAL ================= */

document.addEventListener("DOMContentLoaded", () => {

    const overlay =
        document.getElementById("taskModalOverlay");

    const closeBtn =
        document.getElementById("closeTaskModalBtn");

    /* FECHAR NO X */
    closeBtn?.addEventListener("click", closeTaskModal);

    /* FECHAR CLICANDO FORA */
    overlay?.addEventListener("click", (e) => {

        if (e.target.id === "taskModalOverlay") {
            closeTaskModal();
        }
    });
	
	/* ================= UPLOAD DE ANEXO ================= */

	/*async function uploadAttachment(taskId) {*/
		window.uploadAttachment = async function (taskId) {

	    const input =
	        document.getElementById(
	            "taskAttachmentFile"
	        );

	    if (!input || input.files.length === 0) {

	        alert("Selecione um arquivo.");

	        return;
	    }

	    const formData =
	        new FormData();

	    formData.append(
	        "file",
	        input.files[0]
	    );

	    try {

	        const response =
	            await fetch(

	                `/attachments/upload/${taskId}`,

	                {
	                    method: "POST",

	                    body: formData
	                }
	            );

	        if (response.ok) {

	            alert(
	                "Arquivo enviado com sucesso."
	            );

				const updatedTask =
				    await refreshTask(taskId);

				if (updatedTask) {

				    renderTaskModal(updatedTask);

				}

	        } else {

	            alert(
	                "Erro ao enviar arquivo."
	            );
	        }

	    } catch (e) {

	        console.error(e);

	        alert(
	            "Erro ao enviar arquivo."
	        );
	    }
	}

});
