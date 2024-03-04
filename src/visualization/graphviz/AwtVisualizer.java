///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package visualization.graphviz;
//
//import evaluation.ConfusionMatrix;
//import guru.nidi.graphviz.engine.Engine;
//import guru.nidi.graphviz.engine.Format;
//import guru.nidi.graphviz.engine.Graphviz;
//import guru.nidi.graphviz.engine.GraphvizV8Engine;
//import java.awt.image.BufferedImage;
//import javax.swing.ImageIcon;
//import javax.swing.JFrame;
//import javax.swing.JLabel;
//import javax.swing.JScrollPane;
//
///**
// *
// * @author rabcan
// */
//public class AwtVisualizer extends JFrame{
// 
//     public static void display(Graphviz g) {
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Windows".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(ConfusionMatrix.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(ConfusionMatrix.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(ConfusionMatrix.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(ConfusionMatrix.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//
//        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                BufferedImage image = g.engine(Engine.DOT).render(Format.PNG).toImage();
//
//                JFrame frame = new JFrame();
//                frame.setTitle("FDT");
//                frame.setSize(image.getWidth(), image.getHeight());
//                JLabel label = new JLabel();
//                frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
//                label.setIcon(new ImageIcon(image));
//                frame.add(new JScrollPane(label));
//                frame.setLocationRelativeTo(null);
//                frame.pack();
//                frame.setVisible(true);
//            }
//        });
//
//    }
//    
//}
