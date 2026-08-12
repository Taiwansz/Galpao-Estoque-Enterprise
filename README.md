# Galpão OS Enterprise — Sistema Completo de Gestão de Estoque em Java

Sistema Full-Stack completo de controle de estoque e equipe de galpão industrial, desenvolvido em **Java puríssimo (Java 17/21)** com **Web Server HTTP embutido**, **APIs REST** e **Interface Web Dark Mode (Bento Grid)**.

---

## 🚀 Tecnologias Utilizadas
- **Backend Core:** Java 17/21 (Orientação a Objetos, Classes Abstratas, Polymorphism, Concurrency com `ConcurrentHashMap`).
- **Servidor HTTP & APIs REST:** `com.sun.net.httpserver.HttpServer` nativo do Java (Zero frameworks externos pesados).
- **Frontend UI:** HTML5 + Vanilla CSS Moderno (Glassmorphism & Bento Grid) + JavaScript (Fetch API em tempo real).

---

## 📦 Regras de Negócio Implementadas
1. **Classe `Produto`:** Cadastro obrigatório de nome e preço. O saldo de estoque inicia **rigorosamente zerado (`0`)**.
2. **Superclasse Abstrata `Funcionario`:** Superclasse que define a estrutura de dados de colaboradores (`nome`, `salario`) e o contrato de métodos abstratos `getCargo()` e `getAtividadeAtual()`.
3. **Subclasse `FuncProducao`:** Representa operadores de produção com turno e setor de atuação.
4. **Subclasse `Gestor`:** Representa a gerência de operações do galpão.
5. **Dashboard KPI & Alertas:** Cálculos em tempo real de valor patrimonial total em estoque e alertas automáticos de estoque baixo.

---

## 🛠️ Como Executar

### 1. Compilar o Projeto
```bash
javac -d bin -sourcepath src src/server/WebServer.java src/domain/*.java
```

### 2. Rodar o Servidor Java
```bash
java -cp bin server.WebServer
```

### 3. Acessar a Interface no Navegador
Abra no seu navegador: **`http://localhost:8080`**
