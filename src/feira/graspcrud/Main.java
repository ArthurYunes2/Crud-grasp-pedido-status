package feira.graspcrud;

import feira.graspcrud.controller.PedidoController;
import feira.graspcrud.repository.PedidoRepository;
import feira.graspcrud.repository.StatusPedidoRepository;
import feira.graspcrud.repository.json.PedidoRepositoryJson;
import feira.graspcrud.repository.json.StatusPedidoRepositoryJson;
import feira.graspcrud.service.PedidoService;
import feira.graspcrud.service.StatusPedidoService;

import java.util.Scanner;

/**
 * Ponto de entrada da aplicação — sistema de gestão de pedidos da feira livre.
 *
 * <p>Padrão GRASP: Creator — esta classe é responsável por instanciar e
 * conectar todas as dependências (repositórios, serviços e controller),
 * realizando a composição manual sem uso de framework de injeção de dependências.
 *
 * <p>A ordem de criação respeita as dependências:
 * repositórios → serviços → controller.
 */
public class Main {

    /**
     * Método principal que inicializa e conecta todos os componentes da aplicação.
     *
     * @param args argumentos de linha de comando (não utilizados)
     */
    public static void main(String[] args) {
        // Repositórios (infraestrutura)
        StatusPedidoRepository statusRepo = new StatusPedidoRepositoryJson();
        PedidoRepository pedidoRepo = new PedidoRepositoryJson(statusRepo);

        // Serviços (aplicação)
        StatusPedidoService statusService = new StatusPedidoService(statusRepo, pedidoRepo);
        PedidoService pedidoService = new PedidoService(pedidoRepo, statusRepo);

        // Controller (interface textual)
        Scanner scanner = new Scanner(System.in);
        PedidoController controller = new PedidoController(pedidoService, statusService, scanner);

        System.out.println("Bem-vindo ao Sistema de Gestão de Pedidos da Feira Livre!");
        System.out.println("Dados carregados de: data/pedidos.json e data/status-pedido.json");

        controller.iniciar();

        scanner.close();
    }
}
