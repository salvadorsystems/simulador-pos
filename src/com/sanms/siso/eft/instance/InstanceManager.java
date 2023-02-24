package com.sanms.siso.eft.instance;

import com.sanms.siso.eft.model.Stream;
import com.sanms.siso.eft.processor.ParametrosOperacion;
import com.sanms.siso.eft.proxy.Proxy;
import com.sanms.siso.eft.proxy.ProxyCommResult;
import com.sanms.siso.eft.proxy.ProxyResult;
import com.sanms.siso.eft.utils.Constantes;
import com.sanms.siso.eft.view.ProcesosMC;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.JTable;
import com.sanms.siso.formatter.Field;
import com.sanms.siso.formatter.Template;
import com.sanms.siso.tools.TemplateTool;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsReportConfiguration;
import org.springframework.util.ResourceUtils;
import org.apache.log4j.Logger;

/**
 *
 * @author salvador
 */
public class InstanceManager extends Thread {

    private static final Logger log = Logger.getLogger(InstanceManager.class);

    private String txnName;
    private int numTxn;
    private String instance;
    private JTable tableRequest;
    private JTable tableResponse;
    private JTable tableStatus;
    private List<Object[]> listObject;
    private ArrayList<Field> listField;
    private ArrayList<Field> listFieldResponse;
    private SimpleDateFormat time;
    private boolean running;

    private String rutaParametros;
    private List<Stream> listStream;
    private int errorCount = 0;
    private int succesCount = 0;
    private int i;
    private String template;
    private String rutaTemplate;

    private Proxy proxy;
    private List<String> listThreadId;
    private List<Integer> listSocketId;

    public DefaultTableModel tableModelRequest;
    public DefaultTableModel tableModelResponse;
    public DefaultTableModel tableModelStatus;
    private TableColumnModel columnModelRequest;
    private TableColumnModel columnModelResponse;

    public InstanceManager(int i, String instance, String rutaParametros, String rutaTemplate, List<Stream> listStream) {
        this.i = i;
        this.instance = instance;
        this.rutaParametros = rutaParametros;
        this.rutaTemplate = rutaTemplate;
        this.listStream = listStream;
    }

    public InstanceManager() {
    }

    public String getTxnName() {
        return txnName;
    }

    public void setTxnName(String txnName) {
        this.txnName = txnName;
    }

    public int getNumTxn() {
        return numTxn;
    }

    public void setNumTxn(int numTxn) {
        this.numTxn = numTxn;
    }

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    public JTable getTableRequest() {
        return tableRequest;
    }

    public void setTableRequest(JTable tableRequest) {
        this.tableRequest = tableRequest;
    }

    public JTable getTableResponse() {
        return tableResponse;
    }

    public void setTableResponse(JTable tableResponse) {
        this.tableResponse = tableResponse;
    }

    public JTable getTableStatus() {
        return tableStatus;
    }

    public void setTableStatus(JTable tableStatus) {
        this.tableStatus = tableStatus;
    }

    public List<Object[]> getListObject() {
        return listObject;
    }

    public void setListObject(List<Object[]> listObject) {
        this.listObject = listObject;
    }

    public SimpleDateFormat getTime() {
        return time;
    }

    public void setTime(SimpleDateFormat time) {
        this.time = time;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public String getRutaParametros() {
        return rutaParametros;
    }

    public String getRutaTemplate() {
        return rutaTemplate;
    }

    public List<Stream> getListStream() {
        return listStream;
    }

    public Proxy getProxy() {
        return proxy;
    }

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }

    public String getTemplate() {

        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public List<String> getListThreadId() {
        return listThreadId;
    }

    public void setListThreadId(List<String> listThreadId) {
        this.listThreadId = listThreadId;
    }

    public List<Integer> getListSocketId() {
        return listSocketId;
    }

    public void setListSocketId(List<Integer> listSocketId) {
        this.listSocketId = listSocketId;
    }

    public DefaultTableModel getTableModelRequest() {
        return tableModelRequest;
    }

    public void setTableModelRequest(DefaultTableModel tableModelRequest) {
        this.tableModelRequest = tableModelRequest;
    }

    public DefaultTableModel getTableModelResponse() {
        return tableModelResponse;
    }

    public void setTableModelResponse(DefaultTableModel tableModelResponse) {
        this.tableModelResponse = tableModelResponse;
    }

    public DefaultTableModel getTableModelStatus() {
        return tableModelStatus;
    }

    public void setTableModelStatus(DefaultTableModel tableModelStatus) {
        this.tableModelStatus = tableModelStatus;
    }

    public TableColumnModel getColumnModelRequest() {
        return columnModelRequest;
    }

    public void setColumnModelRequest(TableColumnModel columnModelRequest) {
        this.columnModelRequest = columnModelRequest;
    }

    public TableColumnModel getColumnModelResponse() {
        return columnModelResponse;
    }

    public void setColumnModelResponse(TableColumnModel columnModelResponse) {
        this.columnModelResponse = columnModelResponse;
    }

    @Override
    public void run() {
        for (int count = 0; count < getNumTxn(); count++) {
            if (execute() == 0) {
                log.info("Instancia " + getInstance() + " se envio el mensaje : " + count);
                ProcesosMC.jMenuPDF.setEnabled(true);
                ProcesosMC.jMenuXLS.setEnabled(true);
                tableModelStatus.setValueAt(count + 1, i, 4);
            } else {
                JOptionPane.showMessageDialog(null, "Ups!, No se pudo completar el envio");
                log.info("Ups!, No se pudo completar el envio");
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException ex) {
                log.error(ex);
            }
            tableModelRequest.setNumRows(listField.size());
            tableModelRequest.setColumnCount(5);
            columnModelRequest.getColumn(0).setPreferredWidth(120);
            columnModelRequest.getColumn(1).setPreferredWidth(50);
            columnModelRequest.getColumn(2).setPreferredWidth(50);
            columnModelRequest.getColumn(3).setPreferredWidth(60);
            columnModelRequest.getColumn(4).setPreferredWidth(200);
            int i = 0;
            for (Field field : listField) {
                if (!field.getValue().isEmpty()) {
                    tableModelRequest.setValueAt(field.getAlias(), i, 0);
                    tableModelRequest.setValueAt(field.getIsoBit(), i, 1);
                    tableModelRequest.setValueAt(field.getFormat(), i, 2);
                    tableModelRequest.setValueAt(field.getValueSize(), i, 3);
                    tableModelRequest.setValueAt(field.getValue(), i, 4);
                    i++;
                }
            }
            /**
             *
             */
            tableModelResponse.setNumRows(listFieldResponse.size());
            tableModelResponse.setColumnCount(5);
            columnModelResponse.getColumn(0).setPreferredWidth(120);
            columnModelResponse.getColumn(1).setPreferredWidth(50);
            columnModelResponse.getColumn(2).setPreferredWidth(50);
            columnModelResponse.getColumn(3).setPreferredWidth(60);
            columnModelResponse.getColumn(4).setPreferredWidth(200);
            int j = 0;
            for (Field field : listFieldResponse) {
                if (!field.getValue().isEmpty()) {
                    tableModelResponse.setValueAt(field.getAlias(), j, 0);
                    tableModelResponse.setValueAt(field.getIsoBit(), j, 1);
                    tableModelResponse.setValueAt(field.getFormat(), j, 2);
                    tableModelResponse.setValueAt(field.getValueSize(), j, 3);
                    tableModelResponse.setValueAt(field.getValue(), j, 4);
                    j++;
                }
            }

        }

    }

    public int execute() {
        String plantilla = null;
        String request = null;
        ParametrosOperacion parametrosOperacion = new ParametrosOperacion(getRutaParametros());
        Map<String, Map<String, Map<String, String>>> templateMapList = TemplateTool.setup(getRutaTemplate());
        Map<String, Map<String, Map<String, String>>> templateMapListResponse = TemplateTool.setup(getRutaTemplate());
        Template req;
        try {
            req = parametrosOperacion.obtenerParametrosCmpl(getListStream(), getRutaTemplate());
            request = req.generateStream();
            log.info("SRQ : " + "[" + request + "]");
        } catch (FileNotFoundException ex) {
            log.error(ex);
        }

        for (Stream stream : getListStream()) {
            plantilla = stream.getTemplate();
        }
        ProcesosMC.txtRequerimiento.setText(request);
        Template reqFormat = TemplateTool.createTemplate(templateMapList, plantilla);
        reqFormat.saveFromBuffer(request);

        listField = reqFormat.getFieldList();

        ProxyResult apiResult = new ProxyResult();
        ProxyCommResult resultProxy = getProxy().process(request, apiResult);
        log.info("SRS : " + "[" + resultProxy.getStringResponse() + "]");

        Template reqFormatResponse = TemplateTool.createTemplate(templateMapListResponse, plantilla);
        reqFormatResponse.saveFromBuffer(resultProxy.getStringResponse());
        listFieldResponse = reqFormatResponse.getFieldList();

        ProcesosMC.txtRespuesta.setText(resultProxy.getStringResponse());
        return resultProxy.getResult();
    }

    public void generarReportePDF() throws JRException, IOException {

        log.info("Se solicito generar PDF");
        File file = ResourceUtils.getFile(Constantes.RUTA_PLANTILLA_PDF);
        final JasperReport report = (JasperReport) JRLoader.loadObject(file);
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("txnName", getTxnName());
        parameters.put("dsInvoice", new JRBeanCollectionDataSource(listFieldResponse, false));
        JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters, new JREmptyDataSource());
        byte[] reporte = JasperExportManager.exportReportToPdf(jasperPrint);
        String encodedString = Base64.getEncoder().encodeToString(reporte);
        File filePDF = new File("../simulador-procesosmc/reportes/reporte.pdf");
        try ( FileOutputStream fos = new FileOutputStream(filePDF);) {
            byte[] decoder = Base64.getDecoder().decode(encodedString);
            fos.write(decoder);
            JOptionPane.showMessageDialog(null, "PDF Se Genero Correctamente en la siguiente Ruta: \n" + filePDF.getCanonicalPath());
            log.info("PDF Se Genero Correctamente en la siguiente Ruta: " + filePDF.getCanonicalPath());
        } catch (IOException err) {
            JOptionPane.showMessageDialog(null, "Error al generar PDF: " + err);
            log.error("Error al generar PDF: " + err);
        }

    }

    public void generarReporteXLS() throws JRException, IOException {
        log.info("Se solicito generar EXCEL");
        File file = ResourceUtils.getFile(Constantes.RUTA_PLANTILLA_XLS);
        final JasperReport report = (JasperReport) JRLoader.loadObject(file);
        HashMap<String, Object> parameters = new HashMap<>();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        parameters.put("txnName", getTxnName());
        parameters.put("dsInvoice", new JRBeanCollectionDataSource(listFieldResponse, false));
        JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters, new JREmptyDataSource());

        JRXlsExporter exporter = new JRXlsExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(baos));
        SimpleXlsReportConfiguration configuration = new SimpleXlsReportConfiguration();
        configuration.setOnePagePerSheet(true);
        configuration.setDetectCellType(true);
        configuration.setCollapseRowSpan(false);
        exporter.setConfiguration(configuration);
        exporter.exportReport();

        byte[] reporte = baos.toByteArray();
        String encodedString = Base64.getEncoder().encodeToString(reporte);
        File fileXLS = new File("../simulador-procesosmc/reportes/reporte.xls");
        try ( FileOutputStream fos = new FileOutputStream(fileXLS);) {
            byte[] decoder = Base64.getDecoder().decode(encodedString);
            fos.write(decoder);
            JOptionPane.showMessageDialog(null, "Excel generado correctamente en la siguiente Ruta: \n" + fileXLS.getCanonicalPath());
            log.info("Excel generado correctamente en la siguiente Ruta: " + fileXLS.getCanonicalPath());
        } catch (IOException err) {
            JOptionPane.showMessageDialog(null, "Error al generar Excel: " + err);
            log.error("Error al generar Excel: " + err);
        }
    }
}
