const API_URL = 'http://localhost:8080/api';
let todosProdutos = [];

document.addEventListener('DOMContentLoaded', () => {
    initNavigation();
    carregarTudo();
});

function initNavigation() {
    const navItems = document.querySelectorAll('.nav-item');
    navItems.forEach(item => {
        item.addEventListener('click', () => {
            navItems.forEach(n => n.classList.remove('active'));
            item.classList.add('active');

            const tabId = item.getAttribute('data-tab');
            document.querySelectorAll('.tab-content').forEach(t => t.classList.remove('active'));
            document.getElementById(`tab-${tabId}`).classList.add('active');

            const titles = {
                dashboard: ['Painel Geral de Operações', 'Monitoramento de estoque em tempo real e controle de equipe'],
                estoque: ['Gestão de Estoque', 'Cadastro de produtos e histórico de movimentações'],
                equipe: ['Equipe do Galpão', 'Funcionários registrados e hierarquia de produção/gestão']
            };
            document.getElementById('page-title').textContent = titles[tabId][0];
            document.getElementById('page-subtitle').textContent = titles[tabId][1];
        });
    });
}

async function carregarTudo() {
    await Promise.all([
        carregarDashboard(),
        carregarProdutos(),
        carregarEquipe()
    ]);
}

async function carregarDashboard() {
    try {
        const res = await fetch(`${API_URL}/dashboard`);
        const stats = await res.json();

        document.getElementById('kpi-valor-total').textContent = `R$ ${stats.valorTotalEstoque.toLocaleString('pt-BR', { minimumFractionDigits: 2 })}`;
        document.getElementById('kpi-total-produtos').textContent = stats.totalProdutos;
        document.getElementById('kpi-total-itens').textContent = stats.totalItensFisicos;
        document.getElementById('kpi-alertas').textContent = stats.alertasEstoqueBaixo;
    } catch (e) {
        console.error('Erro ao carregar estatísticas do dashboard:', e);
    }
}

async function carregarProdutos() {
    try {
        const res = await fetch(`${API_URL}/produtos`);
        todosProdutos = await res.json();
        renderizarTabelasProdutos(todosProdutos);
    } catch (e) {
        console.error('Erro ao carregar produtos:', e);
    }
}

function renderizarTabelasProdutos(lista) {
    // Tabela Dashboard
    const tbodyDash = document.getElementById('table-dashboard-produtos');
    tbodyDash.innerHTML = lista.slice(0, 5).map(p => `
        <tr>
            <td><code>${p.id}</code></td>
            <td><strong>${p.nome}</strong></td>
            <td>${p.categoria}</td>
            <td>R$ ${p.preco.toFixed(2)}</td>
            <td><strong>${p.quantidadeEmEstoque}</strong> un.</td>
            <td>
                <span class="status-tag ${p.estoqueBaixo ? 'baixo' : 'normal'}">
                    ${p.estoqueBaixo ? '⚠️ ESTOQUE BAIXO' : '🟢 OK'}
                </span>
            </td>
        </tr>
    `).join('');

    // Tabela Estoque Completa
    const tbodyEstoque = document.getElementById('table-estoque-produtos');
    tbodyEstoque.innerHTML = lista.map(p => `
        <tr>
            <td><code>${p.id}</code></td>
            <td><strong>${p.nome}</strong></td>
            <td>${p.categoria}</td>
            <td>R$ ${p.preco.toFixed(2)}</td>
            <td><strong>${p.quantidadeEmEstoque}</strong> un.</td>
            <td>R$ ${p.valorTotal.toFixed(2)}</td>
            <td>
                <span class="status-tag ${p.estoqueBaixo ? 'baixo' : 'normal'}">
                    ${p.estoqueBaixo ? '⚠️ BAIXO (' + p.quantidadeEmEstoque + '/' + p.estoqueMinimo + ')' : '🟢 NORMAL'}
                </span>
            </td>
            <td>
                <button class="btn btn-secondary btn-sm" onclick="abrirModalMovimentar('${p.id}', '${p.nome}', 'ENTRADA')">
                    ➕ Entrada
                </button>
                <button class="btn btn-secondary btn-sm" onclick="abrirModalMovimentar('${p.id}', '${p.nome}', 'SAIDA')">
                    ➖ Saída
                </button>
            </td>
        </tr>
    `).join('');
}

function filtrarProdutos() {
    const termo = document.getElementById('search-input').value.toLowerCase();
    const filtrados = todosProdutos.filter(p => 
        p.nome.toLowerCase().includes(termo) || 
        p.categoria.toLowerCase().includes(termo) ||
        p.id.toLowerCase().includes(termo)
    );
    renderizarTabelasProdutos(filtrados);
}

async function carregarEquipe() {
    try {
        const res = await fetch(`${API_URL}/funcionarios`);
        const equipe = await res.json();
        const grid = document.getElementById('grid-equipe');

        grid.innerHTML = equipe.map(f => `
            <div class="funcionario-card">
                <h4>${f.nome}</h4>
                <div class="cargo">${f.cargo} | Salário: R$ ${f.salario.toFixed(2)}</div>
                <div class="atividade">${f.atividade}</div>
            </div>
        `).join('');
    } catch (e) {
        console.error('Erro ao carregar equipe:', e);
    }
}

function openModal(id) {
    document.getElementById(id).classList.add('active');
}

function closeModal(id) {
    document.getElementById(id).classList.remove('active');
}

async function salvarProduto(e) {
    e.preventDefault();
    const payload = {
        nome: document.getElementById('input-nome').value,
        categoria: document.getElementById('input-categoria').value,
        preco: document.getElementById('input-preco').value,
        estoqueMinimo: document.getElementById('input-minimo').value
    };

    try {
        const res = await fetch(`${API_URL}/produtos`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (res.ok) {
            closeModal('modal-novo-produto');
            document.getElementById('form-novo-produto').reset();
            carregarTudo();
        }
    } catch (err) {
        console.error('Erro ao cadastrar produto:', err);
    }
}

function abrirModalMovimentar(id, nome, tipo) {
    document.getElementById('mov-produto-id').value = id;
    document.getElementById('mov-tipo').value = tipo;
    document.getElementById('modal-mov-title').textContent = `Movimentar: ${nome}`;
    openModal('modal-movimentar');
}

async function executarMovimentacao(e) {
    e.preventDefault();
    const payload = {
        id: document.getElementById('mov-produto-id').value,
        tipo: document.getElementById('mov-tipo').value,
        quantidade: document.getElementById('mov-quantidade').value
    };

    try {
        const res = await fetch(`${API_URL}/produtos/movimentar`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (res.ok) {
            closeModal('modal-movimentar');
            document.getElementById('form-movimentar').reset();
            carregarTudo();
        }
    } catch (err) {
        console.error('Erro ao movimentar estoque:', err);
    }
}
