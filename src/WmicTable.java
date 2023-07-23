import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class WmicTable {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Tabela WMIC");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Criar um modelo de tabela
        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("Caption");
        tableModel.addColumn("DriveType");

        // Preencher a tabela com os dados obtidos do comando wmic
        populateTableModel(tableModel);

        // Criar a tabela com o modelo
        JTable table = new JTable(tableModel);

        // Adicionar a tabela a um painel com barras de rolagem
        JScrollPane scrollPane = new JScrollPane(table);

        // Adicionar o painel ao frame
        frame.getContentPane().add(scrollPane);

        // Ajustar o tamanho do frame e exibi-lo
        frame.pack();
        frame.setVisible(true);
    }

    private static void populateTableModel(DefaultTableModel tableModel) {
        try {
            // Executar o comando wmic
            Process process = Runtime.getRuntime().exec("wmic logicaldisk get caption,drivetype");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            // Ignorar a primeira linha (cabeçalho)
            reader.readLine();

            // Preencher o modelo da tabela com os dados obtidos
            String line;
            while ((line = reader.readLine()) != null) {
                String[] columns = line.trim().split("\\s+");

                if (columns.length == 2) {
                    String caption = columns[0];
                    int driveType = Integer.parseInt(columns[1]);

                    // Mapear o valor DriveType para "Local" ou "Rede"
                    String driveTypeText = driveType == 3 ? "Local" : driveType == 4 ? "Rede" : "Desconhecido";

                    tableModel.addRow(new Object[]{caption, driveTypeText});
                }
            }

            // Fechar o leitor e aguardar o término do processo
            reader.close();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
