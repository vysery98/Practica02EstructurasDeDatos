package operacionesLQ;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.HashMap;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

// Proceso Gráfico

class ArbolExpresionGrafico extends JPanel {

    private Arbol miArbol;
    private HashMap posicionNodos = null;
    private HashMap subtreeSizes = null;
    private boolean dirty = true;
    private int parent2child = 15, child2child = 25;
    private Dimension empty = new Dimension(0, 0);
    private FontMetrics fm = null;

    public ArbolExpresionGrafico(Arbol miArbol) {
        this.miArbol = miArbol;
        this.setBackground(Color.lightGray);
        posicionNodos = new HashMap();
        subtreeSizes = new HashMap();
        dirty = true;
        repaint();
    }

    private void calcularPosiciones() {
        posicionNodos.clear();
        subtreeSizes.clear();
        NodoArbol root = this.miArbol.getRaiz();
        if (root != null) {
            calcularTamañoSubarbol(root);
            calcularPosicion(root, Integer.MAX_VALUE, Integer.MAX_VALUE, 0);
        }
    }

    private Dimension calcularTamañoSubarbol(NodoArbol n) {
        if (n == null) {
            return new Dimension(0, 0);
        }

        Dimension ld = calcularTamañoSubarbol(n.getNodoIzq());
        Dimension rd = calcularTamañoSubarbol(n.getNodoDer());

        int h = fm.getHeight() + parent2child + Math.max(ld.height, rd.height);
        int w = ld.width + child2child + rd.width;

        Dimension d = new Dimension(w, h);
        subtreeSizes.put(n, d);

        return d;
    }

    private void calcularPosicion(NodoArbol n, int left, int right, int top) {
        if (n == null) {
            return;
        }

        Dimension ld = (Dimension) subtreeSizes.get(n.getNodoIzq());
        if (ld == null) {
            ld = empty;
        }

        Dimension rd = (Dimension) subtreeSizes.get(n.getNodoDer());
        if (rd == null) {
            rd = empty;
        }

        int center = 0;

        if (right != Integer.MAX_VALUE) {
            center = right - rd.width - child2child / 2;
        } else if (left != Integer.MAX_VALUE) {
            center = left + ld.width + child2child / 2;
        }
        int width = fm.stringWidth(n.getDatos() + "");

        posicionNodos.put(n, new Rectangle(center - width / 2 - 3, top, width + 6, fm.getHeight()));

        calcularPosicion(n.getNodoIzq(), Integer.MAX_VALUE, center - child2child / 2, top + fm.getHeight() + parent2child);
        calcularPosicion(n.getNodoDer(), center + child2child / 2, Integer.MAX_VALUE, top + fm.getHeight() + parent2child);
    }

    private void dibujarArbol(Graphics2D g, NodoArbol n, int puntox, int puntoy, int yoffs) {
        if (n == null) {
            return;
        }
        Rectangle r = (Rectangle) posicionNodos.get(n);
        g.draw(r);
        g.drawString(n.getDatos() + "", r.x + 3, r.y + yoffs);
        if (puntox != Integer.MAX_VALUE) {
            g.drawLine(puntox, puntoy, (int) (r.x + r.width / 2), r.y);
        }
        dibujarArbol(g, n.getNodoIzq(), (int) (r.x + r.width / 2), r.y + r.height, yoffs);
        dibujarArbol(g, n.getNodoDer(), (int) (r.x + r.width / 2), r.y + r.height, yoffs);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        fm = g.getFontMetrics();

        if (dirty) {
            calcularPosiciones();
            dirty = false;
        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(getWidth() / 2, parent2child);
        dibujarArbol(g2d, this.miArbol.getRaiz(), Integer.MAX_VALUE,
                Integer.MAX_VALUE, fm.getLeading() + fm.getAscent());
        fm = null;
    }
}

class SimuladorArbolBinario {

    Arbol miArbol = new Arbol();

    public SimuladorArbolBinario() {
    }

    public JPanel getDibujo() {
        return this.miArbol.getdibujo();
    }
    
    public boolean insertar(int valor){
        return (this.miArbol.agregar(valor));
    }
    
    public String borrar(int dato)
    {
        Integer x = this.miArbol.borrar(dato);
        if (x == 0)
            return ("No existe el dato en el arbol");
       return ("Borrado el dato: " + x.toString());
    }
    
    
}

// Proceso Normal
class NodoArbol {

    private int datos; // valor del nodo

    public void setDatos(int datos) {
        this.datos = datos;
    }
    private NodoArbol nodoIzq; // nodo izquierdo
    private NodoArbol nodoDer; // nodo derecho

    public void setNodoIzq(NodoArbol nodoIzq) {
        this.nodoIzq = nodoIzq;
    }

    public void setNodoDer(NodoArbol nodoDer) {
        this.nodoDer = nodoDer;
    }

    public int getDatos() {
        return datos;
    }

    public NodoArbol getNodoIzq() {
        return nodoIzq;
    }

    public NodoArbol getNodoDer() {
        return nodoDer;
    }

    public NodoArbol(int datosNodo) {
        datos = datosNodo;
        nodoIzq = nodoDer = null;
    }

    
}

class Arbol {

    private NodoArbol raiz;

    public void setRaiz(NodoArbol raiz) {
        this.raiz = raiz;
    }
    private String cadena = "";

    public NodoArbol getRaiz() {
        return raiz;
    }
    
    public void limpieza() {
        cadena = "";
    }

    public String getCadena() {
        return cadena;
    }

    public Arbol() {
        raiz = null;
    }
    
    public boolean agregar(int dato) {
        NodoArbol nuevo = new NodoArbol(dato);
        insertar(nuevo, raiz);
        return true;
    }
    
    public void insertar(NodoArbol nuevo, NodoArbol pivote) {
        if (this.raiz == null) {
            raiz = nuevo;
        } else {
            if (nuevo.getDatos() <= pivote.getDatos()) {
                if (pivote.getNodoIzq()== null) {
                    pivote.setNodoIzq(nuevo);
                } else {
                    insertar(nuevo, pivote.getNodoIzq());
                }
            } else {
                if (pivote.getNodoDer()== null) {
                    pivote.setNodoDer(nuevo);
                } else {
                    insertar(nuevo, pivote.getNodoDer());
                }
            }
        }
        
    }
    public int borrar(int x) {
        if (!this.buscar(x)) {
            return 0;
        }

        NodoArbol z = borrar(this.raiz, x);
        this.setRaiz(z);
        return x;

    }

    private NodoArbol borrar(NodoArbol r, int x) {
        if (r == null) {
            return null;//<--Dato no encontrado		
        }
        int compara = ((Comparable) r.getDatos()).compareTo(x);
        if (compara > 0) {
            r.setNodoIzq(borrar(r.getNodoIzq(), x));
        } else if (compara < 0) {
            r.setNodoDer(borrar(r.getNodoDer(), x));
        } else {
            System.out.println("Dato encontrado: " + Integer.toString(x));
            if (r.getNodoIzq()!= null && r.getNodoDer()!= null) {
                /*
		 *	Buscar el menor de los derechos y lo intercambia por el dato
		 *	que desea borrar. La idea del algoritmo es que el dato a borrar 
		 *	se coloque en una hoja o en un nodo que no tenga una de sus ramas.
		 **/
                NodoArbol cambiar = buscarMin(r.getNodoDer());
                int aux = cambiar.getDatos();
                cambiar.setDatos(r.getDatos());
                r.setDatos(aux);
                r.setNodoDer(borrar(r.getNodoDer(), x));
                System.out.println("2 ramas");
            } else {
                r = (r.getNodoIzq()!= null) ? r.getNodoIzq() : r.getNodoDer();
                System.out.println("Reduciendo árbol...");
            }
        }
        return r;
    }
    
    /*
    Métodos para encontrar el nodo para borrarlo
    */
    
    public boolean buscar(int x) {
        return (buscar(this.raiz, x));

    }

    private boolean buscar(NodoArbol r, int x) {
        if (r == null) {
            return (false);
        }
        int compara = ((Comparable) r.getDatos()).compareTo(x);
        if (compara > 0) {
            return (buscar(r.getNodoIzq(), x));
        } else if (compara < 0) {
            return (buscar(r.getNodoDer(), x));
        } else {
            return (true);
        }
    }
    /*
    Retorna el menor nodo del árbol
    */
    private NodoArbol buscarMin(NodoArbol r) {
        for (; r.getNodoIzq()!= null; r = r.getNodoIzq());
        return (r);
    }

    

    

    public void recorridoPreorden() {
        ayudantePreorden(raiz);
    }

    private void ayudantePreorden(NodoArbol nodo) {
        if (nodo == null) {
            return;
        }
        cadena = String.format("%s%d ", cadena, nodo.getDatos());
        ayudantePreorden(nodo.getNodoIzq());
        ayudantePreorden(nodo.getNodoDer());
    }

    public void recorridoInorden() {
        ayudanteInorden(raiz);
    }

    private void ayudanteInorden(NodoArbol nodo) {
        if (nodo == null) {
            return;
        }
        ayudanteInorden(nodo.getNodoIzq());
        cadena = String.format("%s%d ", cadena, nodo.getDatos());
        ayudanteInorden(nodo.getNodoDer());
    }

    public void recorridoPostorden() {
        ayudantePostorden(raiz);
    }

    private void ayudantePostorden(NodoArbol nodo) {
        if (nodo == null) {
            return;
        }
        ayudantePostorden(nodo.getNodoIzq());
        ayudantePostorden(nodo.getNodoDer());
        cadena = String.format("%s%d ", cadena, nodo.getDatos());
    }
    
    public JPanel getdibujo() {
        return new ArbolExpresionGrafico(this);
    }
}

// INTERFAZ GRAFICA
public class lfquizhpe1_ABB extends javax.swing.JFrame {

    private SimuladorArbolBinario simulador = new SimuladorArbolBinario();
    Arbol arbol = new Arbol();
    int valor;

    public lfquizhpe1_ABB() {
        initComponents();
        this.setBackground(Color.DARK_GRAY);
        this.inicializar(false);
        JOptionPane.showMessageDialog(null, "BIENVENIDO !\nNota: Los datos a "
                + "ingresar solo deben ser enteros");
    }

    private void inicializar(boolean disponible) {
        this.preOrd_Button.setEnabled(disponible);
        this.inOrd_Button.setEnabled(disponible);
        this.postOrd_Button.setEnabled(disponible);
    }

    private void limpiar() {
        this.insert_txtField.setText("");
    }

    public void complementos() {
        this.grafica();
    }

    private void grafica() {
        this.jDesktopPane1.removeAll();
        Rectangle tamaño = this.ABB_internalFrame.getBounds();
        this.ABB_internalFrame = null;
        this.ABB_internalFrame = new JInternalFrame("Árbol de Búsqueda Binaria", true);
        this.jDesktopPane1.add(this.ABB_internalFrame, JLayeredPane.DEFAULT_LAYER);
        this.ABB_internalFrame.setVisible(true);
        this.ABB_internalFrame.setBounds(tamaño);
        this.ABB_internalFrame.setEnabled(false);
        this.ABB_internalFrame.add(this.simulador.getDibujo(), BorderLayout.CENTER);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        operaciones_jPanel = new javax.swing.JPanel();
        insert_txtField = new javax.swing.JTextField();
        insert_btn = new javax.swing.JButton();
        eliminarNodo = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        preOrd_Button = new javax.swing.JButton();
        inOrd_Button = new javax.swing.JButton();
        postOrd_Button = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jDesktopPane1 = new javax.swing.JDesktopPane();
        ABB_internalFrame = new javax.swing.JInternalFrame();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("UNIVERSIDAD TÉCNICA PARTICULAR DE LOJA");
        setBackground(new java.awt.Color(153, 204, 255));
        setResizable(false);

        operaciones_jPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Operaciones ABB", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.ABOVE_TOP));

        insert_btn.setText("INSERTAR");
        insert_btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                insert_btnActionPerformed(evt);
            }
        });

        eliminarNodo.setText("Eliminar");
        eliminarNodo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eliminarNodoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout operaciones_jPanelLayout = new javax.swing.GroupLayout(operaciones_jPanel);
        operaciones_jPanel.setLayout(operaciones_jPanelLayout);
        operaciones_jPanelLayout.setHorizontalGroup(
            operaciones_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(operaciones_jPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(insert_txtField)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(operaciones_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(insert_btn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(eliminarNodo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        operaciones_jPanelLayout.setVerticalGroup(
            operaciones_jPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, operaciones_jPanelLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(insert_btn)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(eliminarNodo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, operaciones_jPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(insert_txtField, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28))
        );

        jPanel1.setBackground(new java.awt.Color(0, 102, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "R E C O R R I D O S", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Tahoma", 1, 14))); // NOI18N

        preOrd_Button.setText("Pre-Orden");
        preOrd_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                preOrd_ButtonActionPerformed(evt);
            }
        });

        inOrd_Button.setText("In-Orden");
        inOrd_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inOrd_ButtonActionPerformed(evt);
            }
        });

        postOrd_Button.setText("Post-Orden");
        postOrd_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                postOrd_ButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(inOrd_Button, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(preOrd_Button, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(postOrd_Button, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(preOrd_Button, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11)
                .addComponent(inOrd_Button, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(postOrd_Button, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jLabel1.setFont(new java.awt.Font("Sitka Small", 1, 13)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("ÁRBOL DE BÚSQUEDA BINARIO");
        jLabel1.setBorder(javax.swing.BorderFactory.createCompoundBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED), javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED)));

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Luis Fernando Quizhpe Espinosa");
        jLabel2.setBorder(javax.swing.BorderFactory.createMatteBorder(2, 2, 2, 2, new java.awt.Color(0, 153, 204)));

        ABB_internalFrame.setForeground(new java.awt.Color(153, 0, 153));
        ABB_internalFrame.setTitle("G R Á F I C O");
        ABB_internalFrame.setVisible(true);

        javax.swing.GroupLayout ABB_internalFrameLayout = new javax.swing.GroupLayout(ABB_internalFrame.getContentPane());
        ABB_internalFrame.getContentPane().setLayout(ABB_internalFrameLayout);
        ABB_internalFrameLayout.setHorizontalGroup(
            ABB_internalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 694, Short.MAX_VALUE)
        );
        ABB_internalFrameLayout.setVerticalGroup(
            ABB_internalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        jDesktopPane1.setLayer(ABB_internalFrame, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jDesktopPane1Layout = new javax.swing.GroupLayout(jDesktopPane1);
        jDesktopPane1.setLayout(jDesktopPane1Layout);
        jDesktopPane1Layout.setHorizontalGroup(
            jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jDesktopPane1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(ABB_internalFrame)
                .addContainerGap())
        );
        jDesktopPane1Layout.setVerticalGroup(
            jDesktopPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jDesktopPane1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(ABB_internalFrame)
                .addContainerGap())
        );

        jLabel3.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("ESTRUCTURA DE DATOS");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jDesktopPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(operaciones_jPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 57, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(operaciones_jPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(74, 74, 74))
            .addComponent(jDesktopPane1)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void preOrd_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_preOrd_ButtonActionPerformed
        simulador.miArbol.recorridoPreorden();
        JOptionPane.showMessageDialog(null, "Recorrido PreOrden = " + simulador.miArbol.getCadena());
        simulador.miArbol.limpieza();
    }//GEN-LAST:event_preOrd_ButtonActionPerformed

    private void inOrd_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inOrd_ButtonActionPerformed
        simulador.miArbol.recorridoInorden();
        JOptionPane.showMessageDialog(null, "Recorrido InOrden = " + simulador.miArbol.getCadena());
        simulador.miArbol.limpieza();
    }//GEN-LAST:event_inOrd_ButtonActionPerformed

    private void postOrd_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_postOrd_ButtonActionPerformed
        simulador.miArbol.recorridoPostorden();
        JOptionPane.showMessageDialog(null, "Recorrido PostOrden = " + simulador.miArbol.getCadena());
        simulador.miArbol.limpieza();
    }//GEN-LAST:event_postOrd_ButtonActionPerformed

    private void insert_btnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insert_btnActionPerformed
        try {
            // Ingreso de valores para gráfica
            int dato = Integer.parseInt(insert_txtField.getText());
            if (this.simulador.insertar(dato)) {
                JOptionPane.showMessageDialog(null, "El dato fue insertado correctamente", " ...", 1);
                this.inicializar(true);

                complementos();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Valor inválido, intente nuevamente.");
        }

        // Proceso.arbol.insertarNodo(valor);
    }//GEN-LAST:event_insert_btnActionPerformed

    private void eliminarNodoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eliminarNodoActionPerformed
        int dato = Integer.parseInt(insert_txtField.getText());
        this.simulador.borrar(dato);
        this.grafica();
    }//GEN-LAST:event_eliminarNodoActionPerformed

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(lfquizhpe1_ABB.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(lfquizhpe1_ABB.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(lfquizhpe1_ABB.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(lfquizhpe1_ABB.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new lfquizhpe1_ABB().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JInternalFrame ABB_internalFrame;
    private javax.swing.JButton eliminarNodo;
    private javax.swing.JButton inOrd_Button;
    private javax.swing.JButton insert_btn;
    private javax.swing.JTextField insert_txtField;
    private javax.swing.JDesktopPane jDesktopPane1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel operaciones_jPanel;
    private javax.swing.JButton postOrd_Button;
    private javax.swing.JButton preOrd_Button;
    // End of variables declaration//GEN-END:variables
}
