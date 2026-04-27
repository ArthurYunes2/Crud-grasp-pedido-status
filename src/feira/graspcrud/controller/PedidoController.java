package feira.graspcrud.controller;

import feira.graspcrud.domain.Pedido;
import feira.graspcrud.domain.StatusPedido;
import feira.graspcrud.dto.PedidoRequest;
import feira.graspcrud.dto.StatusPedidoRequest;
import feira.graspcrud.exception.RegraNegocioException;
import feira.graspcrud.service.PedidoService;
import feira.graspcrud.service.StatusPedidoService;

import java.util.List;
import java.util.Scanner;

/**
 * Controller responsável por receber as entradas do menu textual e
 * delegar as operações aos serviços correspondentes.
 *
 * <p>Padrão GRASP: Controller — ponto de entrada das interações do usuário.
 * Não implementa nenhuma regra de negócio; apenas lê entradas, converte
 * para DTOs, chama os serviços e exibe os resultados no terminal.
 *
 * <p>Padrão GRASP: High Cohesion — cada método privado cuida de uma
 * operação específica do menu, mantendo o código organizado e coeso.
 */
public class PedidoController {

    private final PedidoService pedidoService;
    private final StatusPedidoService statusService;
    private final Scanner scanner;

    /**
     * Constrói o controller com os serviços necessários.
     *
     * @param pedidoService  serviço de Pedido
     * @param statusService  serviço de StatusPedido
     * @param scanner        leitor de entrada do terminal
     */
    public PedidoController(PedidoService pedidoService, StatusPedidoService statusService, Scanner scanner) {
        this.pedidoService = pedidoService;
        this.statusService = statusService;
        this.scanner = scanner;
    }

    /**
     * Inicia o loop principal do menu textual.
     * Continua exibindo o menu até que o usuário escolha sair.
     */
    public void iniciar() {
        int opcao;
        do {
            exibirMenu();
            opcao = lerInt("Opção: ");
            System.out.println();
            try {
                switch (opcao) {
                    case 1 -> cadastrarStatus();
                    case 2 -> listarStatus();
                    case 3 -> atualizarStatus();
                    case 4 -> removerStatus();
                    case 5 -> cadastrarPedido();
                    case 6 -> listarPedidos();
                    case 7 -> buscarPedidoPorId();
                    case 8 -> atualizarPedido();
                    case 9 -> avancarStatusPedido();
                    case 10 -> removerPedido();
                    case 0 -> System.out.println("Encerrando o sistema. Até logo!");
                    default -> System.out.println("Opção inválida. Tente novamente.");
                }
            } catch (RegraNegocioException e) {
                System.out.println("\n[ERRO] " + e.getMessage());
            } catch (Exception e) {
                System.out.println("\n[ERRO INESPERADO] " + e.getMessage());
            }
            if (opcao != 0) pausar();
        } while (opcao != 0);
    }

    /** Exibe o menu principal no terminal. */
    private void exibirMenu() {
        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║    SISTEMA DE GESTÃO DE PEDIDOS - FEIRA  ║");
        System.out.println("╠══════════════════════════════════════════╣");
        System.out.println("║  --- StatusPedido ---                    ║");
        System.out.println("║  1. Cadastrar StatusPedido               ║");
        System.out.println("║  2. Listar StatusPedido                  ║");
        System.out.println("║  3. Atualizar StatusPedido               ║");
        System.out.println("║  4. Remover StatusPedido                 ║");
        System.out.println("║  --- Pedido ---                          ║");
        System.out.println("║  5. Cadastrar Pedido                     ║");
        System.out.println("║  6. Listar Pedidos                       ║");
        System.out.println("║  7. Buscar Pedido por ID                 ║");
        System.out.println("║  8. Atualizar Pedido                     ║");
        System.out.println("║  9. Avançar Status do Pedido             ║");
        System.out.println("║ 10. Remover Pedido                       ║");
        System.out.println("║  0. Sair                                 ║");
        System.out.println("╚══════════════════════════════════════════╝");
    }

    // ─── StatusPedido ────────────────────────────────────────────────────────

    /**
     * Coleta os dados e cadastra um novo StatusPedido.
     */
    private void cadastrarStatus() {
        System.out.println("=== Cadastrar StatusPedido ===");
        String nome = lerString("Nome: ");
        String descricao = lerString("Descrição (opcional, Enter para pular): ");
        int ordem = lerInt("Ordem de transição (ex.: 1 = primeiro, 2 = segundo): ");

        StatusPedidoRequest req = new StatusPedidoRequest(nome, descricao.isBlank() ? null : descricao, ordem);
        StatusPedido criado = statusService.cadastrar(req);
        System.out.println("\n✔ StatusPedido cadastrado com sucesso: " + criado);
    }

    /**
     * Lista todos os StatusPedido cadastrados.
     */
    private void listarStatus() {
        System.out.println("=== StatusPedido Cadastrados ===");
        List<StatusPedido> lista = statusService.listarTodos();
        if (lista.isEmpty()) {
            System.out.println("Nenhum StatusPedido cadastrado.");
            return;
        }
        lista.forEach(System.out::println);
    }

    /**
     * Atualiza os dados de um StatusPedido existente.
     */
    private void atualizarStatus() {
        System.out.println("=== Atualizar StatusPedido ===");
        listarStatus();
        Long id = lerLong("ID do StatusPedido a atualizar: ");
        String nome = lerString("Novo nome: ");
        String descricao = lerString("Nova descrição (opcional, Enter para pular): ");
        int ordem = lerInt("Nova ordem de transição: ");

        StatusPedidoRequest req = new StatusPedidoRequest(nome, descricao.isBlank() ? null : descricao, ordem);
        StatusPedido atualizado = statusService.atualizar(id, req);
        System.out.println("\n✔ StatusPedido atualizado: " + atualizado);
    }

    /**
     * Remove um StatusPedido, com validação de uso por pedidos.
     */
    private void removerStatus() {
        System.out.println("=== Remover StatusPedido ===");
        listarStatus();
        Long id = lerLong("ID do StatusPedido a remover: ");
        statusService.remover(id);
        System.out.println("\n✔ StatusPedido removido com sucesso.");
    }

    // ─── Pedido ──────────────────────────────────────────────────────────────

    /**
     * Coleta os dados e cadastra um novo Pedido.
     */
    private void cadastrarPedido() {
        System.out.println("=== Cadastrar Pedido ===");
        List<StatusPedido> statusDisponiveis = statusService.listarTodos();
        if (statusDisponiveis.isEmpty()) {
            System.out.println("=== StatusPedido Cadastrados ===");
            System.out.println("Nenhum StatusPedido cadastrado.");
            System.out.println("[AVISO] Cadastre ao menos um StatusPedido antes de criar um pedido.");
            return;
        }
        System.out.println("=== StatusPedido Cadastrados ===");
        statusDisponiveis.forEach(System.out::println);
        String nome = lerString("Nome do pedido: ");
        String descricao = lerString("Descrição (opcional, Enter para pular): ");
        Long statusId = lerLong("ID do StatusPedido: ");

        PedidoRequest req = new PedidoRequest(nome, descricao.isBlank() ? null : descricao, true, statusId);
        Pedido criado = pedidoService.cadastrar(req);
        System.out.println("\n✔ Pedido cadastrado com sucesso: " + criado);
    }

    /**
     * Lista todos os pedidos cadastrados.
     */
    private void listarPedidos() {
        System.out.println("=== Pedidos Cadastrados ===");
        List<Pedido> lista = pedidoService.listarTodos();
        if (lista.isEmpty()) {
            System.out.println("Nenhum pedido cadastrado.");
            return;
        }
        lista.forEach(System.out::println);
    }

    /**
     * Busca e exibe um Pedido pelo seu id.
     */
    private void buscarPedidoPorId() {
        System.out.println("=== Buscar Pedido por ID ===");
        Long id = lerLong("ID do pedido: ");
        Pedido pedido = pedidoService.buscarPorId(id);
        System.out.println("\n" + pedido);
    }

    /**
     * Atualiza os campos básicos de um Pedido (nome, descrição, ativo).
     */
    private void atualizarPedido() {
        System.out.println("=== Atualizar Pedido ===");
        listarPedidos();
        Long id = lerLong("ID do pedido a atualizar: ");
        Pedido atual = pedidoService.buscarPorId(id);
        System.out.println("Pedido atual: " + atual);

        String nome = lerString("Novo nome (Enter para manter '" + atual.getNome() + "'): ");
        if (nome.isBlank()) nome = atual.getNome();

        String descricao = lerString("Nova descrição (Enter para manter atual): ");
        if (descricao.isBlank()) descricao = atual.getDescricao();

        String ativoStr = lerString("Ativo? (s/n, Enter para manter): ");
        Boolean ativo = ativoStr.isBlank() ? atual.getAtivo()
                : ativoStr.equalsIgnoreCase("s");

        PedidoRequest req = new PedidoRequest(nome, descricao, ativo, null);
        Pedido atualizado = pedidoService.atualizar(id, req);
        System.out.println("\n✔ Pedido atualizado: " + atualizado);
    }

    /**
     * Avança o status de um Pedido para um novo StatusPedido,
     * respeitando a regra de transição progressiva.
     */
    private void avancarStatusPedido() {
        System.out.println("=== Avançar Status do Pedido ===");
        listarPedidos();
        Long pedidoId = lerLong("ID do pedido: ");
        Pedido pedido = pedidoService.buscarPorId(pedidoId);
        System.out.println("Status atual: " + (pedido.getStatusPedido() != null ? pedido.getStatusPedido() : "N/A"));

        System.out.println("\nStatus disponíveis (apenas de ordem maior que a atual são válidos):");
        listarStatus();

        Long novoStatusId = lerLong("ID do novo StatusPedido: ");
        Pedido atualizado = pedidoService.avancarStatus(pedidoId, novoStatusId);
        System.out.println("\n✔ Status avançado: " + atualizado);
    }

    /**
     * Remove um Pedido pelo seu id.
     */
    private void removerPedido() {
        System.out.println("=== Remover Pedido ===");
        listarPedidos();
        Long id = lerLong("ID do pedido a remover: ");
        pedidoService.remover(id);
        System.out.println("\n✔ Pedido removido com sucesso.");
    }

    // ─── Utilitários de entrada ───────────────────────────────────────────────

    /**
     * Lê uma linha de texto do terminal.
     *
     * @param prompt mensagem exibida ao usuário
     * @return texto digitado (pode ser vazio)
     */
    private String lerString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    /**
     * Lê um número inteiro do terminal, repetindo em caso de entrada inválida.
     *
     * @param prompt mensagem exibida ao usuário
     * @return inteiro digitado
     */
    private int lerInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String entrada = scanner.nextLine().trim();
            try {
                return Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.println("[AVISO] Informe um número inteiro válido.");
            }
        }
    }

    /**
     * Lê um número Long do terminal, repetindo em caso de entrada inválida.
     *
     * @param prompt mensagem exibida ao usuário
     * @return Long digitado
     */
    private Long lerLong(String prompt) {
        while (true) {
            System.out.print(prompt);
            String entrada = scanner.nextLine().trim();
            try {
                return Long.parseLong(entrada);
            } catch (NumberFormatException e) {
                System.out.println("[AVISO] Informe um número inteiro válido.");
            }
        }
    }

    /**
     * Aguarda o usuário pressionar Enter antes de voltar ao menu.
     */
    private void pausar() {
        System.out.print("\nPressione Enter para continuar...");
        scanner.nextLine();
    }
}
