package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.border.Border;

class RoundedBorder implements Border {
    private int radius;

    public RoundedBorder(int radius) {
        this.radius = radius;
    }

    @Override
    public Insets getBorderInsets(Component c) {
        return new Insets(radius, radius, radius, radius);
    }

    @Override
    public boolean isBorderOpaque() {
        return true;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.BLACK);
        g2d.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
    }
}

class Node {
    int data;
    Node left, right;

    int x, y;

    public Node(int data) {
        this.data = data;
        this.left = null;
        this.right = null;
    }
}

class BinaryTree {
    private Node root;

    public Node getRoot() {
        return root;
    }

    public Node insert(Node node, int val) {
        if (node == null) {
            return new Node(val);
        }
        if (val < node.data) {
            node.left = insert(node.left, val);
        } else if (val > node.data) {
            node.right = insert(node.right, val);
        }
        return node;
    }

    public void clear() {
        root = null;
    }

    public void insert(int val) {
        root = insert(root, val);
    }

    public void eliminar(int val) {
        root = eliminarNodo(root, val);
    }

    public void inOrder(Node node) {
        if (node != null)  {
            inOrder(node.left);
            System.out.print(node.data + " ");
            inOrder(node.right);
        }
    }

    public void reconstruirBalanceado() {
        List<Integer> valores = new ArrayList<>();
        inOrderList(root, valores);
        root = construirBalanceado(valores, 0, valores.size() -1);
    }

    private Node eliminarNodo(Node node, int val) {
        if (node == null) return null;

        if (val < node.data) {
            node.left = eliminarNodo(node.left, val);
        } else if (val > node.data) {
            node.right = eliminarNodo(node.right, val);
        } else {
            //Nodo hoja
            if(node.left == null && node.right == null) {
                return null;
            }
            //Un hijo
            if (node.left == null) {
                return node.right;
            }else if (node.right == null) {
                return node.left;
            }

            node.data = minValor(node.right);
            node.right = eliminarNodo(node.right, node.data);
        }
        return node;
    }

    private int minValor(Node node) {
        int min = node.data;
        while (node.left != null) {
            min = node.left.data;
            node = node.left;
        }
        return min;
    }

    private Node construirBalanceado(List<Integer> valores, int inicio, int fin) {
        if (inicio > fin) return null;

        int medio = (inicio + fin) / 2;
        Node nuevoNodo = new Node(valores.get(medio));

        nuevoNodo.left = construirBalanceado(valores, inicio, medio - 1);
        nuevoNodo.right = construirBalanceado(valores, medio + 1, fin);

        return nuevoNodo;
    }

    private void inOrderList(Node node, List<Integer> valores) {
        if (node != null) {
            inOrderList(node.left, valores);
            valores.add(node.data);
            inOrderList(node.right, valores);
        }
    }

    public void insertWithAnimation(int val, Runnable callback) {
        root = insert(root, val);
        SwingUtilities.invokeLater(callback);
    }

    public List<Integer> inOrderTraversal(Node node) {
        List<Integer> result = new ArrayList<>();
        if (node != null) {
            result.addAll(inOrderTraversal(node.left));
            result.add(node.data);
            result.addAll(inOrderTraversal(node.right));
        }
        return result;
    }

    public List<Integer> preOrderTraversal(Node node) {
        List<Integer> result = new ArrayList<>();
        if (node != null) {
            result.add(node.data);
            result.addAll(preOrderTraversal(node.left));
            result.addAll(preOrderTraversal(node.right));
        }
        return result;
    }

    public List<Integer> postOrderTraversal(Node node) {
        List<Integer> result = new ArrayList<>();
        if (node != null) {
            result.addAll(postOrderTraversal(node.left));
            result.addAll(postOrderTraversal(node.right));
            result.add(node.data);
        }
        return result;
    }

    public int buscarNivel(int valor) {
        return buscarNivelHelper(root, valor, 0);
    }

    private int buscarNivelHelper(Node nodo, int valor, int nivelActual) {
        if (nodo == null) {
            return -1; // No encontrado
        }
        if (valor == nodo.data) {
            return nivelActual + 1; // Ajuste: +1 para que raíz sea nivel 1
        }
        if (valor < nodo.data) {
            return buscarNivelHelper(nodo.left, valor, nivelActual + 1);
        } else {
            return buscarNivelHelper(nodo.right, valor, nivelActual + 1);
        }
    }

    public Node buscar(int valor) {
        return buscarRecursivo(root, valor);
    }

    private Node buscarRecursivo(Node actual, int valor) {
        if (actual == null || actual.data == valor) {
            return actual;
        }
        if (valor < actual.data) {
            return buscarRecursivo(actual.left, valor);
        } else {
            return buscarRecursivo(actual.right, valor);
        }
    }
}

class TreePanel extends JPanel {
    public Node nodoResaltado = null;

    private Node root;

    private JPopupMenu menu;
    private Node nodoSeleccionado;
    private BinaryTree tree;
    private JLabel lblResultado;

    public TreePanel(BinaryTree tree) {
        this.tree = tree;

        // Crear el menú de opciones
        menu = new JPopupMenu();
        JMenuItem eliminarOpcion = new JMenuItem("Eliminar");
        eliminarOpcion.addActionListener(e -> {
            if (nodoSeleccionado != null) {
                tree.eliminar(nodoSeleccionado.data);
                repaint(); // Redibujar el árbol
            }
        });
        menu.add(eliminarOpcion);

        // Agregar listener de mouse
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) { // Click derecho
                    nodoSeleccionado = encontrarNodo(root, e.getX(), e.getY());
                    if (nodoSeleccionado != null) {
                        menu.show(TreePanel.this, e.getX(), e.getY());
                    }
                }
            }
        });

        //Recorridos Label
        lblResultado = new JLabel("Resultado: ");
        lblResultado.setFont(new Font("Arial", Font.BOLD, 14));
        lblResultado.setForeground(Color.BLACK);
        this.setLayout(null);
        lblResultado.setBounds(10, 10, 300, 20);
        this.add(lblResultado);
    }

    public void actualizarResultado(String recorrido, List<Integer> valores) {
        lblResultado.setText(recorrido + ":  " + valores.toString());
    }

    public void resaltarNodo(Node nodo) {
        this.nodoResaltado = nodo;
        repaint();
    }

    private Node encontrarNodo(Node node, int x, int y) {
        if (node == null) return null;

        int radio = 20;
        int dx = x - node.x;
        int dy = y - node.y;

        if(Math.sqrt(dx * dx + dy * dy) <= radio) {
            return node;
        }

        Node izquierda = encontrarNodo(node.left, x, y);
        if (izquierda != null) return izquierda;

        return encontrarNodo(node.right, x, y);
    }

    public void setRoot(Node root) {
        this.root = root;
        calcularPosiciones(root, getWidth() / 2, 50, getWidth() / 4);
        repaint();
    }

    private void calcularPosiciones(Node node, int x, int y, int offset) {
        if (node == null) {
            return;
        }

        node.x = x;
        node.y = y;

        // Calcular posiciones de los hijos
        int newOffset = Math.max(offset / 2, 40); // Reducir el espacio en cada nivel

        if (node.left != null) {
            calcularPosiciones(node.left, x - newOffset, y + 80, newOffset);
        }
        if (node.right != null) {
            calcularPosiciones(node.right, x + newOffset, y + 80, newOffset);
        }
    }

    private void dibujarNodo(Graphics g, Node nodo, int x, int y) {
        if (nodo == null) return;

        // Configurar el color de resaltado
        g.setColor(Color.RED);
        g.fillOval(x - 20, y - 20, 40, 40); // Círculo del nodo

        // Configurar el color del texto dentro del nodo resaltado
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        FontMetrics fm = g.getFontMetrics();
        String text = String.valueOf(nodo.data);
        int textX = x - fm.stringWidth(text) / 2;
        int textY = y + fm.getAscent() / 2 - 4;
        g.drawString(text, textX, textY);
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        //antialiasing
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Dibujar el árbol
        drawTree(g2, getWidth() / 2, 60, root, getWidth() / 4);

        if (nodoResaltado != null) {
            g.setColor(Color.RED);
            dibujarNodo(g, nodoResaltado, nodoResaltado.x, nodoResaltado.y);
        }
    }

    private void drawTree(Graphics2D g, int x, int y, Node node, int offset) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (node == null) return;

        int nodeSize = 40;

        // Fuente numeros
        Font font = new Font("Arial", Font.BOLD, 16);
        g.setFont(font);

        // Color de los nodos
        Color nodeColor = new Color(52, 152, 219);
        Color borderColor = new Color(41, 128, 185);

        // Dibuja las conexiones
        g.setStroke(new BasicStroke(2));
        g.setColor(Color.BLACK);
        if (node.left != null) {
            int newOffset = Math.max(offset / 2, 40);
            int childX = x - newOffset;
            int childY = y + 80;

            g.setColor(Color.BLACK);
            g.drawLine(x, y, childX, childY);

            drawTree(g, childX, childY, node.left, newOffset);
        }
        if (node.right != null) {
            int newOffset = Math.max(offset / 2, 40);
            int childX = x + newOffset;
            int childY = y + 80;

            g.setColor(Color.BLACK);
            g.drawLine(x, y, childX, childY);

            drawTree(g, childX, childY, node.right, newOffset);
        }

        // Dibuja la sombra del nodo
        g.setColor(new Color(0, 0, 0, 60));
        g.fillOval(x - nodeSize / 2 + 3, y - nodeSize / 2 + 3, nodeSize, nodeSize);

        // Dibuja el nodo
        g.setColor(nodeColor);
        g.fillOval(x - nodeSize / 2, y - nodeSize / 2, nodeSize, nodeSize);

        // Dibuja el borde del nodo
        g.setColor(borderColor);
        g.drawOval(x - nodeSize / 2, y - nodeSize / 2, nodeSize, nodeSize);

        // Dibuja el número en el centro
        g.setColor(Color.WHITE);
        String text = String.valueOf(node.data);
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();
        g.drawString(text, x - textWidth / 2, y + textHeight / 4);
    }


}


public class BTSApp {

    public static void inOrder(Node raiz, List<Integer> lista) {
        if (raiz != null) {
            inOrder(raiz.left, lista);
            lista.add(raiz.data);
            inOrder(raiz.right, lista);
        }
    }

    public static List<Integer> leerValoresDesdeArchivo(String rutaArchivo) {
        List<Integer> valores = new ArrayList<>();
        File archivo = new File(rutaArchivo);

        // Si el archivo no existe, créalo con valores predeterminados
        if (!archivo.exists()) {
            try (FileWriter fw = new FileWriter(archivo)) {
                fw.write("8,3,10,1,6,14,4,7,13"); // Valores iniciales
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error al crear el archivo.", "Error", JOptionPane.ERROR_MESSAGE);
                return valores;
            }
        }

        // Ahora lee el archivo
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea = br.readLine(); // Leer la primera línea
            if (linea != null) {
                String[] numeros = linea.split(","); // Separar por comas
                for (String num : numeros) {
                    valores.add(Integer.parseInt(num.trim())); // Convertir a entero
                }
            }
        } catch (IOException | NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Error al leer el archivo.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        return valores;
    }

    public static void guardarResultadoEnArchivo(String tipoBusqueda, List<Integer> resultado) {
        String rutaArchivo = "resultados.txt";

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(rutaArchivo, true))) {
            bw.write(tipoBusqueda + ": " + resultado.toString());
            bw.newLine();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al guardar el resultado.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void ordenarArbol(BinaryTree tree, TreePanel panel) {
        if (tree.getRoot() == null) return;

        List<Integer> valoresOrdenados = new ArrayList<>();
        inOrder(tree.getRoot(), valoresOrdenados);

        tree.clear();
        for (int valor : valoresOrdenados) {
            tree.insert(valor);

        }

        panel.setRoot(tree.getRoot());
        panel.repaint();
    }

    public static void main(String[] args) {
        BinaryTree tree = new BinaryTree();
        TreePanel panel = new TreePanel(tree);
        JFrame frame = new JFrame("Árbol Binario Animado");

        frame.setLayout(new BorderLayout());

        //Panel de búsqueda
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBusqueda.setBackground(new Color(230, 230, 230));

        // Boton InOrden
        JButton btnInOrden = crearBoton("InOrden");
        btnInOrden.addActionListener(e -> {
            List<Integer> result = tree.inOrderTraversal(tree.getRoot());
            panel.actualizarResultado("InOrden", result);
            guardarResultadoEnArchivo("InOrden", result); // Guarda el resultado en el archivo
        });

        // Boton PreOrden
        JButton btnPreOrden = crearBoton("PreOrden");
        btnPreOrden.addActionListener(e -> {
            List<Integer> result = tree.preOrderTraversal(tree.getRoot());
            panel.actualizarResultado("PreOrden", result);
            guardarResultadoEnArchivo("PreOrden", result); // Guarda el resultado en el archivo
        });

        // Boton PostOrden
        JButton btnPostOrden = crearBoton("PostOrden");
        btnPostOrden.addActionListener(e -> {
            List<Integer> result = tree.postOrderTraversal(tree.getRoot());
            panel.actualizarResultado("PostOrden", result);
            guardarResultadoEnArchivo("PostOrden", result); // Guarda el resultado en el archivo
        });


        // agregar botones al panel de búsqueda
        panelBusqueda.add(btnInOrden);
        panelBusqueda.add(btnPreOrden);
        panelBusqueda.add(btnPostOrden);

        //Agregar componentes al frame
        frame.add(panelBusqueda, BorderLayout.NORTH);
        frame.add(panel, BorderLayout.CENTER);

        //Configuración ventana
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setVisible(true);

        // Panel para la inserción de nodos
        JPanel panelInsertar = new JPanel();
        panelInsertar.setLayout(new FlowLayout(FlowLayout.RIGHT));
        panelInsertar.setBackground(new Color(240, 240, 240));

        // txt para ingresar el valor
        JTextField campoValor = new JTextField(5);
        campoValor.setFont(new Font("Arial", Font.PLAIN, 14));

        // Boton insertar
        JButton btnInsertar = new JButton("Insertar");
        btnInsertar.setFocusPainted(false);
        btnInsertar.setBorderPainted(false);
        btnInsertar.setBackground(new Color(50, 50, 50));
        btnInsertar.setForeground(Color.white);
        btnInsertar.setFont(new Font("Arial", Font.BOLD, 14));
        btnInsertar.setPreferredSize(new Dimension(150, 40));
        btnInsertar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnInsertar.setOpaque(true);
        btnInsertar.setBorder(new RoundedBorder(20));

        btnInsertar.addActionListener(e -> {
            try {
                int valor = Integer.parseInt(campoValor.getText().trim());
                tree.insert(valor);  // Insertar en el árbol
                panel.setRoot(tree.getRoot());  // Actualizar la visualización
                panel.repaint();
                campoValor.setText("");  // Limpiar el campo de texto
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Ingrese un número válido", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Agregar el panel al frame
        frame.add(panelInsertar, BorderLayout.EAST);

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setFocusPainted(false);
        btnBuscar.setBorderPainted(false);
        btnBuscar.setBackground(new Color(50, 50, 50));
        btnBuscar.setForeground(Color.white);
        btnBuscar.setFont(new Font("Arial", Font.BOLD, 14));
        btnBuscar.setPreferredSize(new Dimension(150, 40)); // Tamaño
        btnBuscar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBuscar.setOpaque(true);
        btnBuscar.setBorder(new RoundedBorder(20));

        JLabel labelNivel = new JLabel("Nivel: -");
        labelNivel.setFont(new Font("Arial", Font.BOLD, 14));

// Acción del botón
        btnBuscar.addActionListener(e -> {
            try {
                int valor = Integer.parseInt(campoValor.getText().trim());
                Node nodoEncontrado = tree.buscar(valor);
                if (nodoEncontrado != null) {
                    panel.resaltarNodo(nodoEncontrado);
                    int nivel = tree.buscarNivel(valor); // Obtener el nivel
                    labelNivel.setText("Nivel: " + nivel); // Actualizar la etiqueta
                } else {
                    labelNivel.setText("Nivel: -"); // Resetear si no se encuentra
                    JOptionPane.showMessageDialog(frame, "El valor no está en el árbol", "No encontrado", JOptionPane.WARNING_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Ingrese un número válido", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panelInsertar.setLayout(new BoxLayout(panelInsertar, BoxLayout.Y_AXIS));

        // Cambiar el layout de panelInsertar a GridBagLayout (más control)
        panelInsertar.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Márgenes uniformes
        gbc.fill = GridBagConstraints.HORIZONTAL; // Ajuste horizontal

// Fila 1: Label "Valor" + Campo de texto
        gbc.gridx = 0; // Columna 0
        gbc.gridy = 0; // Fila 0
        panelInsertar.add(new JLabel("Valor:"), gbc);

        gbc.gridx = 1; // Columna 1
        gbc.gridwidth = 2; // Ocupa 2 columnas
        panelInsertar.add(campoValor, gbc);
        gbc.gridwidth = 1; // Resetear

// Fila 2: Botones Insertar y Buscar
        gbc.gridx = 0; // Columna 0
        gbc.gridy = 1; // Fila 1
        gbc.gridwidth = 1;
        panelInsertar.add(btnInsertar, gbc);

        gbc.gridx = 1; // Columna 1
        panelInsertar.add(btnBuscar, gbc);

// Fila 3: Label del nivel (centrado)
        gbc.gridx = 0; // Columna 0
        gbc.gridy = 2; // Fila 2
        gbc.gridwidth = 2; // Ocupa 2 columnas
        gbc.anchor = GridBagConstraints.CENTER; // Centrar el texto
        panelInsertar.add(labelNivel, gbc);

        //Animación
        String rutaArchivo = "datos.txt"; // Ruta del archivo
        List<Integer> values = leerValoresDesdeArchivo(rutaArchivo);

        Timer timer = new Timer(1000, new ActionListener() {
            private int index = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (index < values.size()) {
                    int value = values.get(index);
                    tree.insertWithAnimation(value, () -> {
                        panel.setRoot(tree.getRoot());
                        panel.repaint();
                        tree.inOrder(tree.getRoot());
                        System.out.println();
                    });
                    index++;
                } else {
                    ((Timer) e.getSource()).stop();
                }
            }
        });
        timer.start();
    }

    //botones estilizados
    private static JButton crearBoton(String texto) {
        JButton boton = new JButton(texto);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setBackground(new Color(50, 50, 50));
        boton.setForeground(Color.white);
        boton.setFont(new Font("Arial", Font.BOLD, 12));
        boton.setPreferredSize(new Dimension(100, 30)); // Tamaño más compacto
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setOpaque(true);
        return boton;
    }
}