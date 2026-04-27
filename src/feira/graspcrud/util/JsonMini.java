package feira.graspcrud.util;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * Utilitário de serialização e desserialização JSON escrito em Java puro,
 * sem dependência de bibliotecas externas como Jackson ou Gson.
 *
 * <p>Padrão GRASP: Pure Fabrication — esta classe não representa nenhum
 * conceito do domínio da feira livre. Foi criada exclusivamente para
 * manter o domínio e os serviços livres de detalhes de persistência,
 * encapsulando toda a lógica de leitura e escrita de JSON.
 *
 * <p>Suporta apenas o subconjunto de JSON necessário para esta aplicação:
 * objetos simples com campos String, Long, int, Boolean e LocalDate (como String).
 */
public class JsonMini {

    /**
     * Lê o conteúdo de um arquivo JSON e retorna como String.
     * Retorna string vazia se o arquivo não existir.
     *
     * @param caminho caminho do arquivo JSON
     * @return conteúdo do arquivo ou string vazia
     */
    public static String lerArquivo(String caminho) {
        Path path = Path.of(caminho);
        if (!Files.exists(path)) return "";
        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao ler arquivo JSON: " + caminho, e);
        }
    }

    /**
     * Escreve o conteúdo informado em um arquivo, criando-o se necessário.
     *
     * @param caminho  caminho do arquivo de destino
     * @param conteudo texto a ser gravado
     */
    public static void escreverArquivo(String caminho, String conteudo) {
        try {
            Path path = Path.of(caminho);
            Files.createDirectories(path.getParent());
            Files.writeString(path, conteudo, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao escrever arquivo JSON: " + caminho, e);
        }
    }

    /**
     * Serializa uma lista de mapas (cada mapa representando um objeto) para JSON.
     *
     * @param lista lista de objetos representados como Map&lt;String, Object&gt;
     * @return string JSON formatada
     */
    public static String serializarLista(List<Map<String, Object>> lista) {
        if (lista == null || lista.isEmpty()) return "[]";
        StringBuilder sb = new StringBuilder("[\n");
        for (int i = 0; i < lista.size(); i++) {
            sb.append("  ").append(serializarObjeto(lista.get(i)));
            if (i < lista.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Serializa um mapa de campos para um objeto JSON inline.
     *
     * @param mapa campos do objeto
     * @return string JSON do objeto
     */
    public static String serializarObjeto(Map<String, Object> mapa) {
        StringBuilder sb = new StringBuilder("{");
        List<String> chaves = new ArrayList<>(mapa.keySet());
        for (int i = 0; i < chaves.size(); i++) {
            String chave = chaves.get(i);
            Object valor = mapa.get(chave);
            sb.append("\"").append(chave).append("\":");
            sb.append(valorParaJson(valor));
            if (i < chaves.size() - 1) sb.append(",");
        }
        sb.append("}");
        return sb.toString();
    }

    /**
     * Converte um valor Java para sua representação JSON.
     *
     * @param valor o valor a converter
     * @return representação JSON como String
     */
    private static String valorParaJson(Object valor) {
        if (valor == null) return "null";
        if (valor instanceof String) return "\"" + escapar((String) valor) + "\"";
        if (valor instanceof Boolean || valor instanceof Number) return valor.toString();
        if (valor instanceof Map) return serializarObjeto((Map<String, Object>) valor);
        return "\"" + escapar(valor.toString()) + "\"";
    }

    /**
     * Escapa caracteres especiais em strings JSON.
     *
     * @param texto texto a escapar
     * @return texto com caracteres especiais escapados
     */
    private static String escapar(String texto) {
        return texto.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    }

    /**
     * Desserializa um array JSON em uma lista de mapas de campos.
     *
     * @param json string JSON contendo um array de objetos
     * @return lista de mapas, um por objeto JSON
     */
    public static List<Map<String, Object>> desserializarLista(String json) {
        List<Map<String, Object>> resultado = new ArrayList<>();
        if (json == null || json.isBlank()) return resultado;
        json = json.trim();
        if (!json.startsWith("[")) return resultado;

        // Remove colchetes externos e divide os objetos
        json = json.substring(1, json.lastIndexOf(']')).trim();
        List<String> objetos = dividirObjetos(json);
        for (String obj : objetos) {
            obj = obj.trim();
            if (!obj.isEmpty()) {
                resultado.add(desserializarObjeto(obj));
            }
        }
        return resultado;
    }

    /**
     * Divide o conteúdo de um array JSON em strings de objetos individuais,
     * respeitando o aninhamento de chaves.
     *
     * @param conteudo conteúdo interno de um array JSON
     * @return lista de strings, cada uma representando um objeto JSON
     */
    private static List<String> dividirObjetos(String conteudo) {
        List<String> objetos = new ArrayList<>();
        int profundidade = 0;
        int inicio = -1;
        boolean dentroDeString = false;
        for (int i = 0; i < conteudo.length(); i++) {
            char c = conteudo.charAt(i);
            if (c == '\\' && dentroDeString) { i++; continue; } // pula escape
            if (c == '"') { dentroDeString = !dentroDeString; continue; }
            if (dentroDeString) continue; // ignora { e } dentro de strings
            if (c == '{') {
                if (profundidade == 0) inicio = i;
                profundidade++;
            } else if (c == '}') {
                profundidade--;
                if (profundidade == 0 && inicio != -1) {
                    objetos.add(conteudo.substring(inicio, i + 1));
                    inicio = -1;
                }
            }
        }
        return objetos;
    }

    /**
     * Desserializa um objeto JSON em um mapa de campos.
     *
     * @param json string JSON de um único objeto
     * @return mapa com os campos e valores do objeto
     */
    public static Map<String, Object> desserializarObjeto(String json) {
        Map<String, Object> mapa = new LinkedHashMap<>();
        json = json.trim();
        if (!json.startsWith("{")) return mapa;
        json = json.substring(1, json.lastIndexOf('}')).trim();

        int i = 0;
        while (i < json.length()) {
            // Pula espaços e vírgulas
            while (i < json.length() && (json.charAt(i) == ',' || Character.isWhitespace(json.charAt(i)))) i++;
            if (i >= json.length()) break;

            // Lê chave (respeitando escapes dentro do nome)
            if (json.charAt(i) != '"') break;
            int fimChave = i + 1;
            while (fimChave < json.length()) {
                char ck = json.charAt(fimChave);
                if (ck == '\\') { fimChave += 2; continue; }
                if (ck == '"') break;
                fimChave++;
            }
            String chave = json.substring(i + 1, fimChave);
            i = fimChave + 1;

            // Pula ':'
            while (i < json.length() && (json.charAt(i) == ':' || Character.isWhitespace(json.charAt(i)))) i++;

            // Lê valor
            Object valor;
            if (json.charAt(i) == '"') {
                // Avança respeitando escapes: \" não encerra a string
                int fimValor = i + 1;
                while (fimValor < json.length()) {
                    char c2 = json.charAt(fimValor);
                    if (c2 == '\\') { fimValor += 2; continue; } // pula par de escape
                    if (c2 == '"') break;
                    fimValor++;
                }
                valor = json.substring(i + 1, fimValor)
                        .replace("\\\"", "\"")
                        .replace("\\n", "\n")
                        .replace("\\r", "\r")
                        .replace("\\t", "\t")
                        .replace("\\\\", "\\");
                i = fimValor + 1;
            } else if (json.charAt(i) == '{') {
                int profundidade = 0, j = i;
                while (j < json.length()) {
                    if (json.charAt(j) == '{') profundidade++;
                    else if (json.charAt(j) == '}') { profundidade--; if (profundidade == 0) break; }
                    j++;
                }
                valor = desserializarObjeto(json.substring(i, j + 1));
                i = j + 1;
            } else if (json.startsWith("null", i)) {
                valor = null;
                i += 4;
            } else if (json.startsWith("true", i)) {
                valor = true;
                i += 4;
            } else if (json.startsWith("false", i)) {
                valor = false;
                i += 5;
            } else {
                int j = i;
                while (j < json.length() && json.charAt(j) != ',' && json.charAt(j) != '}') j++;
                valor = json.substring(i, j).trim();
                i = j;
            }
            mapa.put(chave, valor);
        }
        return mapa;
    }

    /**
     * Converte um valor de mapa (lido do JSON) para Long.
     *
     * @param valor o valor lido do JSON
     * @return Long correspondente, ou null se o valor for null
     */
    public static Long toLong(Object valor) {
        if (valor == null) return null;
        return Long.parseLong(valor.toString().trim());
    }

    /**
     * Converte um valor de mapa (lido do JSON) para Integer.
     *
     * @param valor o valor lido do JSON
     * @return Integer correspondente, ou 0 se o valor for null
     */
    public static int toInt(Object valor) {
        if (valor == null) return 0;
        return Integer.parseInt(valor.toString().trim());
    }

    /**
     * Converte um valor de mapa (lido do JSON) para Boolean.
     *
     * @param valor o valor lido do JSON
     * @return Boolean correspondente, ou false se o valor for null
     */
    public static Boolean toBoolean(Object valor) {
        if (valor == null) return false;
        if (valor instanceof Boolean) return (Boolean) valor;
        return Boolean.parseBoolean(valor.toString().trim());
    }

    /**
     * Converte um valor de mapa (lido do JSON) para String.
     *
     * <p>Nomeado {@code toStr} para evitar conflito com {@link Object#toString()}.
     *
     * @param valor o valor lido do JSON
     * @return String correspondente, ou null se o valor for null
     */
    public static String toStr(Object valor) {
        if (valor == null) return null;
        return valor.toString();
    }
}
