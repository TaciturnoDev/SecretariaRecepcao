let sectors = [];

/* ================= CARREGAR SETORES ================= */

async function carregarSetores() {

    try {

        const response = await fetch("/sectors/with-users");

        if (!response.ok) {
            throw new Error("Erro ao buscar setores");
        }

        sectors = await response.json();

        renderSetores();

    } catch (e) {

        console.error("Erro ao carregar setores:", e);

    }

}

/* ================= RENDER ================= */

function renderSetores() {

    const grid = document.getElementById("sectorGrid");

    if (!grid) return;

    grid.innerHTML = "";

    if (!sectors || sectors.length === 0) {

        grid.innerHTML = `
            <p style="color:#64748b;">
                Nenhum setor encontrado.
            </p>
        `;

        return;
    }

    sectors.forEach(sector => {

        const users = sector.users || [];

        const card = document.createElement("div");

        card.className = "sector-card";

        card.innerHTML = `

            <div class="sector-card-top">

                <div class="sector-card-title">

                    <div class="sector-avatar blue">
                        🏢
                    </div>

                    <div>
                        <h3>${sector.name}</h3>

                        <span class="sector-status active">
                            Ativo
                        </span>
                    </div>

                </div>

            </div>

            <div class="sector-card-body">

                <div class="sector-line">
                    <span>Usuários</span>
                    <strong>${users.length}</strong>
                </div>

                <div class="sector-line">
                    <span>Equipe</span>
                    <strong>
                        ${
                            users.length > 0
                                ? users.map(u => u.name).join(", ")
                                : "—"
                        }
                    </strong>
                </div>

            </div>

            <button class="manage-btn">
                Gerenciar
            </button>

        `;

        grid.appendChild(card);

    });

}

/* ================= INIT ================= */

document.addEventListener("DOMContentLoaded", () => {

    carregarSetores();

});