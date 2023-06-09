package com.sanms.siso.eft.proxy;

import com.sanms.siso.eft.model.Stream;
import com.sanms.siso.eft.processor.Worker;
import com.sanms.siso.eft.utils.Constantes;
import com.sanms.siso.eft.utils.Errores;
import com.sanms.siso.eft.view.PosSimulator;
import java.io.IOException;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import net.sf.jasperreports.engine.JRException;
import org.apache.log4j.Logger;

/**
 *
 * @author salvador
 */
public class ProxySocket {

    private static final Logger log = Logger.getLogger(ProxySocket.class);
    private Worker work;
    private int connectSocket = -1;
    private Proxy proxy[];
    private int num_instances;
    private int num_send_per_instance;
    private String parametersPath;
    private String templatesPath;
    private String configPath;
    private final DefaultTableModel tableModelRequest;
    private final DefaultTableModel tableModelResponse;
    private final DefaultTableModel tableModelStatus;

    private final TableColumnModel columnModelRequest;
    private final TableColumnModel columnModelResponse;
    private final TableColumnModel columnModelStatus;

    public ProxySocket(DefaultTableModel tableModelRequest, DefaultTableModel tableModelResponse, DefaultTableModel tableModelStatus,
            TableColumnModel columnModelRequest, TableColumnModel columnModelResponse, TableColumnModel columnModelStatus) {
        this.tableModelRequest = tableModelRequest;
        this.tableModelResponse = tableModelResponse;
        this.tableModelStatus = tableModelStatus;
        this.columnModelRequest = columnModelRequest;
        this.columnModelResponse = columnModelResponse;
        this.columnModelStatus = columnModelStatus;
    }

    public void setupBack(String enviromentPath, String workPath, String parametersPath, String templatesPath) {
        this.configPath = enviromentPath + "\\" + workPath;
        this.parametersPath = configPath + "\\" + parametersPath;
        this.templatesPath = configPath + "\\" + templatesPath;
    }

    public void setupFront(int numI, int numS) {
        this.num_instances = numI;
        this.num_send_per_instance = numS;
    }

    public void openSocketAny(String ip, String port) {
        proxy = new Proxy[num_instances];
        for (int i = 0; i < num_instances; i++) {
            proxy[i] = new Proxy();
            connectSocket = proxy[i].setup(ip, port, false, 30);
            if (connectSocket == 0) {
                enableComponentes(true, "Disconnect", ip, port, Constantes.PATH_IMG_ON);
            } else {
                log.debug(Errores.ERROR_VALIDACION_OBLIGATORIEDAD_1004.getMensaje());
                JOptionPane.showMessageDialog(null, Errores.ERROR_VALIDACION_OBLIGATORIEDAD_1004.getMensaje(),
                        "Error de conexión", JOptionPane.ERROR_MESSAGE);
                break;
            }
        }
    }

    public void closeSocket() {
        enableComponentes(false, "Connect", "0.0.0.0", "00", Constantes.PATH_IMG_OFF);
        PosSimulator.jMenuPDF.setEnabled(false);
        PosSimulator.jMenuXLS.setEnabled(false);
        PosSimulator.jm_reportStatus.setEnabled(false);
        for (int i = 0; i < num_instances; i++) {
            proxy[i].release();
        }
        for (int i = 0; i < tableModelStatus.getRowCount(); i++) {
            tableModelStatus.setValueAt("Disconnected", i, 3);
        }
    }

    public void sendMessageSocket(String txnName, List<Stream> listStream) {
        work = new Worker(proxy, parametersPath, templatesPath, listStream, tableModelRequest, tableModelResponse, tableModelStatus,
                columnModelRequest, columnModelResponse, columnModelStatus);
        work.setConfigPath(configPath);
        work.setTxnName(txnName);
        work.setNumIns(num_instances);
        work.setNumTxn(num_send_per_instance);
        work.execute();
    }

    public void reportPDF() throws JRException, IOException {
        work.reportPDF();
    }

    public void reportXLS() throws JRException, IOException {
        work.reportXLS();
    }

    public void fullReport() throws JRException, IOException {
        work.reportFull();
    }

    public void enableComponentes(boolean val, String status, String ip, String port, String imgConn) {
        PosSimulator.jm_sendMessage.setEnabled(val);
        PosSimulator.btnSendMessage.setEnabled(val);
        PosSimulator.btnTCPIP.setEnabled(!val);
        PosSimulator.num_ship.setEnabled(!val);
        PosSimulator.jm_tcpip.setEnabled(!val);
        PosSimulator.btnCargar.setEnabled(!val);
        PosSimulator.jm_custom.setEnabled(!val);
        PosSimulator.CboxNumI.setEnabled(!val);
        PosSimulator.btnConnect.setText(status);
        PosSimulator.jm_connect.setText(status);
        PosSimulator.ip_adress.setText(ip);
        PosSimulator.lblPort.setText(port);
        PosSimulator.imgConn.setIcon(new ImageIcon(getClass().getResource(imgConn)));
        if (PosSimulator.jListConfig.isSelectionEmpty()) {
            PosSimulator.jm_sendMessage.setEnabled(false);
            PosSimulator.btnSendMessage.setEnabled(false);
        }
    }
}
