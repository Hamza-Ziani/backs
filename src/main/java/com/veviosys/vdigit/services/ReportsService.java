package com.veviosys.vdigit.services;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.veviosys.vdigit.classes.DocumentReport;
import com.veviosys.vdigit.classes.FolderClass;
import com.veviosys.vdigit.classes.FolderReport;
import com.veviosys.vdigit.classes.FolderReportes;
import com.veviosys.vdigit.classes.JournaReports;
import com.veviosys.vdigit.models.Document;
import com.veviosys.vdigit.models.DocumentAttributeValue;
import com.veviosys.vdigit.models.DocumentFolder;
import com.veviosys.vdigit.models.Folder;
import com.veviosys.vdigit.models.FolderType;
import com.veviosys.vdigit.models.Journal;
import com.veviosys.vdigit.models.ReportAttributesConfig;
import com.veviosys.vdigit.models.User;
import com.veviosys.vdigit.models.receiver;
import com.veviosys.vdigit.repositories.FolderTypeRepo;
import com.veviosys.vdigit.repositories.JournalRepo;
import com.veviosys.vdigit.repositories.ReportAttributesConfigRepo;

import ar.com.fdvs.dj.core.DynamicJasperHelper;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.core.layout.HorizontalBandAlignment;
import ar.com.fdvs.dj.domain.AutoText;
import ar.com.fdvs.dj.domain.BooleanExpression;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.ImageBanner;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.ColumnBuilderException;
import ar.com.fdvs.dj.domain.builders.DynamicReportBuilder;
import ar.com.fdvs.dj.domain.builders.StyleBuilder;
import ar.com.fdvs.dj.domain.constants.Border;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.constants.HorizontalAlign;
import ar.com.fdvs.dj.domain.constants.Transparency;
import ar.com.fdvs.dj.domain.constants.VerticalAlign;
import ar.com.fdvs.dj.domain.entities.Parameter;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;

@Service
public class ReportsService {

    @Autowired
    SearchService searchService;
    @Autowired
    MasterConfigService configService;
    @Autowired
    FolderTypeRepo ftr;
    @Value("${type.dep}")
    private String dep;
    @Value("${type.arr}")
    private String arr;
    @Autowired
    ReportAttributesConfigRepo racr;
    @Autowired
    AlimentationService alimentationService;
    @Autowired
    JournalRepo journalRepo;
    @Autowired
    userService us;

    public JasperPrint getReport(Collection<DocumentReport> list, HashMap<String, String> headers, Folder courrier)
            throws ColumnBuilderException, JRException, ClassNotFoundException {

        Style headerStyle = createHeaderStyle();
        Style detailTextStyle = createDetailTextStyle();
        Style detailNumberStyle = createDetailNumberStyle();
        DynamicReport dynaReport = getReport(courrier, headerStyle, detailTextStyle, detailNumberStyle, headers);
        JasperPrint jp = DynamicJasperHelper.generateJasperPrint(dynaReport, new ClassicLayoutManager(),
                new JRBeanCollectionDataSource(list));

        return jp;
    }

    public Style createHeaderStyle() {
        StyleBuilder sb = new StyleBuilder(true);
        sb.setFont(Font.ARIAL_MEDIUM);
        sb.setBorder(Border.THIN());
        sb.setBorderBottom(Border.THIN());
        sb.setBorderColor(Color.BLACK);
        sb.setBackgroundColor(new Color(0, 102, 153));
        sb.setTextColor(Color.WHITE);
        sb.setHorizontalAlign(HorizontalAlign.CENTER);
        sb.setVerticalAlign(VerticalAlign.MIDDLE);
        sb.setTransparency(Transparency.OPAQUE);
        return sb.build();
    }

    public Style createDetailTextStyle() {
        StyleBuilder sb = new StyleBuilder(true);
        sb.setFont(Font.VERDANA_MEDIUM);
        sb.setBorder(Border.THIN());
        sb.setBorderColor(Color.BLACK);
        sb.setTextColor(Color.BLACK);
        sb.setHorizontalAlign(HorizontalAlign.LEFT);
        sb.setVerticalAlign(VerticalAlign.MIDDLE);
        // sb.setPaddingLeft(5);
        return sb.build();
    }

    public ar.com.fdvs.dj.domain.Style createDetailNumberStyle() {
        StyleBuilder sb = new StyleBuilder(false);
        sb.setFont(Font.VERDANA_MEDIUM);
        sb.setBorder(Border.THIN());
        sb.setBorderColor(Color.BLACK);
        sb.setTextColor(Color.BLACK);
        sb.setHorizontalAlign(HorizontalAlign.LEFT);
        sb.setVerticalAlign(VerticalAlign.MIDDLE);
        sb.setPaddingRight(5);

        return sb.build();
    }

    private AbstractColumn createColumn(String property, Class type,
            String title, int width, Style headerStyle, Style detailStyle)
            throws ColumnBuilderException {
        System.out.println(title + " : : " + property);
        AbstractColumn columnState = ColumnBuilder.getNew()
                .setColumnProperty(property, type.getName()).setTitle(
                        title)
                .setWidth(Integer.valueOf(width))
                .setStyle(detailStyle).setHeaderStyle(headerStyle).build();
        return columnState;
    }

    String dests = "";
    private DynamicReport getReport(Folder f, Style headerStyle, Style detailTextStyle, Style detailNumStyle,
            HashMap<String, String> headers) throws ColumnBuilderException, ClassNotFoundException {

        DynamicReportBuilder report = new DynamicReportBuilder();

        report.addColumn(createColumn("type", String.class, "Type", 30, headerStyle, detailNumStyle));
        for (String key : headers.keySet()) {
            String value = headers.get(key);
            System.out.println(key + " : " + value);
            report.addColumn(createColumn(key, String.class, value, 30, headerStyle, detailNumStyle));
        }

        report.addColumn(createColumn("doc", String.class, "Document", 30, headerStyle, detailNumStyle));

        report.addParameter(new Parameter("foo", "foo"));
        AutoText title = new AutoText("Date :" + f.getDate(), AutoText.POSITION_HEADER, HorizontalBandAlignment.LEFT);
        title.setPrintWhenExpression(printWhereFirstPage());
        AutoText title1 = new AutoText("Référence :" + f.getReference(), AutoText.POSITION_HEADER,
                HorizontalBandAlignment.LEFT);
        title1.setPrintWhenExpression(printWhereFirstPage());
         
        
        for (receiver dest : f.getDest()) {
            dests += dest.getName();
        }
        
        AutoText title2 = new AutoText("Destinataire(s) : " + dests , AutoText.POSITION_HEADER,
                HorizontalBandAlignment.LEFT);
        title2.setPrintWhenExpression(printWhereFirstPage());
        String nt = "Catégorie : " + f.getNatureName();
        AutoText title3 = new AutoText(nt, AutoText.POSITION_HEADER, HorizontalBandAlignment.LEFT);

        title3.setPrintWhenExpression(printWhereFirstPage());

        AutoText title4 = new AutoText("Objet :  " + f.getObjet(), AutoText.POSITION_HEADER,
                HorizontalBandAlignment.RIGHT);
        title4.setPrintWhenExpression(printWhereFirstPage());
        title1.setWidth(600);
        title.setWidth(600);
        title2.setWidth(600);
        title3.setWidth(600);
        title4.setWidth(600);
        report.addAutoText(title);
        report.addAutoText(title1);
        report.addAutoText(title2);
        report.addAutoText(title3);
        report.addAutoText(title4);
        // report.addAutoText(showBatchAutoText);
        StyleBuilder sb = new StyleBuilder(true);
        sb.setFont(Font.ARIAL_MEDIUM_BOLD);
        sb.setBorder(Border.THIN());
        sb.setBorderBottom(Border.THIN());
        sb.setBorderColor(Color.BLACK);
        sb.setBackgroundColor(new Color(0, 123, 255));
        sb.setTextColor(Color.WHITE);
        sb.setHorizontalAlign(HorizontalAlign.CENTER);
        sb.setVerticalAlign(VerticalAlign.MIDDLE);
        sb.setTransparency(Transparency.OPAQUE);
        Style s = sb.build();
        report.setTitle("Bordereau");
        report.setTitleStyle(s);
        report.addFirstPageImageBanner("classpath:acaps.png", 100, 100, ImageBanner.ALIGN_LEFT)
                .addImageBanner("classpath:acaps.png", 100, 100, ImageBanner.ALIGN_LEFT);
        // .addWatermark("CNOPS");

        report.setUseFullPageWidth(true);
        report.addAutoText(
                AutoText.AUTOTEXT_PAGE_X_SLASH_Y,
                AutoText.POSITION_FOOTER,
                AutoText.ALIGMENT_RIGHT);

        report.setIgnorePagination(false);
        dests = "";
        return report.build();
    }

    public BooleanExpression printWhereFirstPage() {
        return new BooleanExpression() {

            @Override
            public Object evaluate(Map fields, Map variables, Map parameters) {
                if (Integer.parseInt(variables.get("PAGE_NUMBER").toString()) == 1)
                    return true;
                return false;
            }
        };
    }

    /// Generation de bordereau Specifique
    public JasperPrint genReports(FolderClass courrier, String secondary)
            throws ColumnBuilderException, JRException, ClassNotFoundException {

        List<FolderReport> l = new ArrayList<FolderReport>();
        List<Folder> list = searchService.searchFoldersAll(courrier, secondary);
        List<String> attrs = new ArrayList<>();
        String config = configService.getMasterConfigByName("BORDEREAU_ATTRIBUTES").getConfigValue();
        List<String> libs = new ArrayList<>();

        HashMap<String, String> params = new HashMap<>();
        System.out.println("Confiiig" + config);

        for (String val : config.split("[||]")) {
            System.out.println("KEEEEEEEEY" + val);
            if (!val.equals(" ") && !val.equals("")) {
                String[] data = val.split("#@#");
                params.put(data[1], data[0]);
                attrs.add(data[1]);
                libs.add(data[1]);
            }

            // data[0]
        }

        HashMap<String, String> parameters = new HashMap<>();
        int index = 1;

        List<String> headers = new ArrayList<>();
        try {
            for (int i = 0; i < list.size(); i++) {
                Class<?> clazz = FolderReport.class;
                Object cc = clazz.newInstance();
                Folder f = list.get(i);
                for (int j = 0; j < attrs.size(); j++) {
                    String attr = attrs.get(j);
                    if (headers.indexOf(attr) == -1) {
                        headers.add(attr);
                        parameters.put("attr" + index, libs.get(j));
                        index += 1;
                    }
                    for (Entry<String, String> entry : parameters.entrySet()) {
                        if (entry.getValue().equals(attr)) {
                            Field f1 = cc.getClass().getDeclaredField(entry.getKey());
                            f1.setAccessible(true);
                            f1.set(cc, f.findValueByName(params, attr));
                        }
                    }

                }

                l.add((FolderReport) cc);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        Style headerStyle = createHeaderStyle();
        Style detailTextStyle = createDetailTextStyle();
        Style detailNumberStyle = createDetailNumberStyle();
        DynamicReport dynaReport = getReportFolder(headerStyle, detailTextStyle, detailNumberStyle, parameters);
        JasperPrint jp = DynamicJasperHelper.generateJasperPrint(dynaReport, new ClassicLayoutManager(),
                new JRBeanCollectionDataSource(l));

        return jp;
    }

    public DynamicReport getReportFolder(Style headerStyle, Style detailTextStyle, Style detailNumStyle,
            HashMap<String, String> headers) throws ColumnBuilderException, ClassNotFoundException {

        DynamicReportBuilder report = new DynamicReportBuilder();
        List<String> sortedKeys = new ArrayList<String>(headers.keySet());
        Collections.sort(sortedKeys);
        for (String key : sortedKeys) {
            String value = headers.get(key);
            report.addColumn(createColumn(key, String.class, value, 30, headerStyle, detailNumStyle));
        }
        StyleBuilder titleStyle = new StyleBuilder(true);
        titleStyle.setHorizontalAlign(HorizontalAlign.CENTER);

        // titleStyle.setFont(new Font(20, Font._FONT_ARIAL, true));

        StyleBuilder subTitleStyle = new StyleBuilder(true);
        subTitleStyle.setHorizontalAlign(HorizontalAlign.CENTER);
        // subTitleStyle.setFont(new Font(Font.MEDIUM, Font._FONT_ARIAL, true));
        // AutoText title1 = new AutoText("Référence :" + f.getAutoRef(),
        // AutoText.POSITION_HEADER,
        // HorizontalBandAlignment.LEFT);

        AutoText title = new AutoText(configService.getMasterConfigByName("BORDEREAU_TITLE").getConfigValue(),
                AutoText.POSITION_HEADER, HorizontalBandAlignment.CENTER);
        title.setPrintWhenExpression(printWhereFirstPage());
        StyleBuilder styleBuilder = new StyleBuilder(true);

        styleBuilder.setPaddingBottom(30);
        Color c = new Color(0, 102, 153);
        styleBuilder.setTextColor(c);
        Font font = new Font();
        font.setBold(true);
        font.setFontSize(20);
        styleBuilder.setFont(font);

        Style style = styleBuilder.build();
        title.setStyle(style);
        title.setWidth(700);
        report.addAutoText(title);

        // report.addte(showBatchAutoText);

        StyleBuilder sb = new StyleBuilder(true);

        sb.setFont(Font.ARIAL_MEDIUM_BOLD);
        sb.setBorder(Border.THIN());
        sb.setBorderBottom(Border.THIN());
        sb.setBorderColor(Color.BLACK);
        sb.setBackgroundColor(new Color(0, 123, 255));
        sb.setTextColor(Color.WHITE);
        sb.setHorizontalAlign(HorizontalAlign.CENTER);
        sb.setVerticalAlign(VerticalAlign.MIDDLE);
        sb.setTransparency(Transparency.OPAQUE);
        Style s = sb.build();
        report.setTitle("Bordereau");
        report.setTitleStyle(s);
        String path = configService.getMasterConfigByName("LOGO_PATH").getConfigValue();
        report.addFirstPageImageBanner(path, 50, 50, ImageBanner.ALIGN_LEFT)
                .addImageBanner(path, 50, 50, ImageBanner.ALIGN_LEFT);
        // .addWatermark("CNOPS");

        report.setUseFullPageWidth(true);
        report.addAutoText(
                AutoText.AUTOTEXT_PAGE_X_SLASH_Y,
                AutoText.POSITION_FOOTER,
                AutoText.ALIGMENT_RIGHT);

        report.setIgnorePagination(false);
        return report.build();
    }

    /*
     * private AbstractColumn createColumnCourrier( HashMap<String,String>
     * parameters,String property, Class type,
     * String title, int width, Style headerStyle, Style detailStyle)
     * throws ColumnBuilderException {
     * System.out.println(title+" : : "+property);
     * AbstractColumn columnState = ColumnBuilder.getNew()
     * .setColumnProperty(property, type.getName()).setTitle(
     * title).setWidth(Integer.valueOf(width))
     * .setStyle(detailStyle).setHeaderStyle(headerStyle).build();
     * return columnState;
     * }
     */

    public Map<String, String> exportCourrier(FolderClass cc,
            String formatFile, Pageable pageable, String secondary) throws JRException, IOException {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        List<HashMap<String, String>> exportedList = new ArrayList<HashMap<String, String>>();
        HashMap<String, Object> params = new HashMap<String, Object>();
        HashMap<String, String> exportedItem = new HashMap<String, String>();
        List<Folder> pageFolder = searchService.searchFoldersAll(cc, secondary);
        String lbl;
        String v;
        User master = us.GetMaster();
        String type;
        String typeName = "N/A";
        if (Objects.nonNull(cc.getType())) {
            FolderType fType = ftr.getOne(cc.getType());
            type = fType.getCat();
            typeName = fType.getName();
            if (type.equals(dep))
                lbl = "Déstinataire";
            else if (type.equals(arr))
                lbl = "Emetteur";

            else
                lbl = "Déstinataire/Emetteur";
        } else
            lbl = "Déstinataire/Emetteur";

        List<FolderReportes> reportData = new ArrayList<FolderReportes>();

        for (Folder folder : pageFolder) {

            if (Objects.nonNull(folder.getReference()))
                exportedItem.put("Ref", String.valueOf(folder.getReference()));
            else
                exportedItem.put("Ref", String.valueOf("N/A"));
            exportedItem.put("Date", String.valueOf(folder.getDate()));
            exportedItem.put("type", String.valueOf(folder.getTypeName()));

            if (Objects.nonNull(folder.getNatureName()))
                exportedItem.put(lbl, folder.getNatureName());
            else {
                exportedItem.put(lbl, "N/A");
            }
            // if (Objects.nonNull(folder.getTypeName()))
            // exportedItem.put(lbl, folder.getNatureName());
            // else {
            // exportedItem.put(lbl, "N/A");
            // }
            String emetOrDest = "";

            if (folder.getDest().size() != 0) {
                             
                      for (receiver dest : folder.getDest()) {
                          emetOrDest += " "+dest.getName();
                    }


            } else if (Objects.nonNull(folder.getEmet__())) {
        
                    emetOrDest = folder.getEmet__();

            } else {
                emetOrDest = "N/A";
            }
            reportData.add(new FolderReportes(folder.getReference(), folder.getNatureName(), folder.getDate(),
                    emetOrDest, folder.getObjet(), lbl, folder.getTypeName()));

            exportedItem.put("Destinataire(s) / Emetteur", String.valueOf(emetOrDest));

            exportedItem.put("Objet", String.valueOf(folder.getObjet()));

            exportedList.add(exportedItem);
            exportedItem = new HashMap<String, String>();
        }

        List<ReportAttributesConfig> reportAttributesConfig = racr.findByActiveAndMaster(1, master.getUserId());
        params.put("typeCourrier", String.valueOf("Type des courriers :    " + typeName));
        for (ReportAttributesConfig ra : reportAttributesConfig) {
            if (ra.getName().equals("typeCourrier")) {
                params.put(ra.getName(), String.valueOf(ra.getLibelle() + " :    " + typeName));
            }
            if (ra.getName().equals("count")) {
                params.put("count", ra.getLibelle() + " :    " + pageFolder.size());
            }
            if (ra.getName().equals("title")) {
                params.put("title", ra.getLibelle());
            }
            // params.put("nature", String.valueOf(lbl));

        }
        JRBeanCollectionDataSource source = new JRBeanCollectionDataSource(reportData);
        params.put("Collection1", source);
        // params.put("typeCourrier", String.valueOf(typeName));
        params.put("nature", String.valueOf(lbl));

        if (alimentationService.getLogo() != null) {
            params.put("logo", alimentationService.getLogo().split(",")[1]);
        }

        // params.put("count",pageFolder.size()+"");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        if (formatFile.equals("pdf")) {

            InputStream resource = new ClassPathResource(
                    "Courriers_report_pdf.jrxml").getInputStream();
            JasperDesign design = JRXmlLoader.load(resource);
            JasperReport jsprep = JasperCompileManager.compileReport(design);
            JasperPrint print = JasperFillManager.fillReport(jsprep, params, new JREmptyDataSource());
            JasperExportManager.exportReportToPdfStream(print, baos);
            
        } else if (formatFile.equals("xl")) {
            InputStream resource = new ClassPathResource(
                    "Courriers_report_xl.jrxml").getInputStream();

            JasperDesign design = JRXmlLoader.load(resource);
            JasperReport jsprep = JasperCompileManager.compileReport(design);
            JasperPrint print = JasperFillManager.fillReport(jsprep, params, new JREmptyDataSource());
            JRXlsxExporter exporter = new JRXlsxExporter();
            SimpleXlsxReportConfiguration reportConfigXLS = new SimpleXlsxReportConfiguration();
            reportConfigXLS.setSheetNames(new String[] { "sheet1" });
            reportConfigXLS.setOnePagePerSheet(false);
            reportConfigXLS.setRemoveEmptySpaceBetweenRows(true);
            exporter.setConfiguration(reportConfigXLS);
            exporter.setExporterInput(new SimpleExporterInput(print));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(baos));

            exporter.exportReport();

        } else {

        }

        baos.close();
        ByteArrayInputStream inStream = new ByteArrayInputStream(baos.toByteArray());
        exportedItem.put("file", Base64.getEncoder().encodeToString(baos.toByteArray()));

        InputStreamResource res = new InputStreamResource(inStream);
        return exportedItem;

    }

    public Map<String, String> exportJournal(Date d1, Date d2) throws JRException, IOException {
	        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
	        List<HashMap<String, String>> exportedList = new ArrayList<HashMap<String, String>>();
	        HashMap<String, Object> params = new HashMap<String, Object>();
	        HashMap<String, String> exportedItem = new HashMap<String, String>();
	        List<Journal> pageJournal = journalRepo.findByDateMaster(us.GetMaster().getUserId(), d1, d2);
	   
	        if(pageJournal.size() == 0) {
	            return null;
	        }
	        User master = us.GetMaster();
	     

	        List<JournaReports> reportData = new ArrayList<JournaReports>();

	        for (Journal journal : pageJournal) {

	           
	            exportedItem.put("Utilisateur", String.valueOf(journal.getUser().getFullName()));
	            exportedItem.put("Secondaire", String.valueOf(journal.getConnectedSacondaryName()));
	            exportedItem.put("Composant", String.valueOf(journal.getComposante()));
	            exportedItem.put("Date", String.valueOf(journal.getDate()));
	            exportedItem.put("type", String.valueOf(journal.getTypeEv()));
	           
	            reportData.add(new JournaReports(journal.getUser().getFullName(), journal.getConnectedSacondaryName(),journal.getDate().toString(),
	                     journal.getComposante(), journal.getTypeEv()));

	            

	            exportedList.add(exportedItem);
	            exportedItem = new HashMap<String, String>();
	        }

	        List<ReportAttributesConfig> reportAttributesConfig = racr.findByActiveAndMaster(1, master.getUserId());  
	        for (ReportAttributesConfig ra : reportAttributesConfig) {
	       
	            if (ra.getName().equals("count")) {
	                params.put("count", ra.getLibelle() + " :    " + pageJournal.size());
	            }
	            if (ra.getName().equals("journal")) {
	                params.put("journal", ra.getLibelle());
	            }
	            // params.put("nature", String.valueOf(lbl));

	        }
	        JRBeanCollectionDataSource source = new JRBeanCollectionDataSource(reportData);
	        params.put("Collection1", source);
	        // params.put("typeCourrier", String.valueOf(typeName));
	       
	       
	        if(alimentationService.getLogo() != null) {
	            params.put("logo", alimentationService.getLogo().split(",")[1]);
	        }
	        

	        // params.put("count",pageFolder.size()+"");

	        ByteArrayOutputStream baos = new ByteArrayOutputStream();

	            InputStream resource = new ClassPathResource(
	                    "Journals_report_pdf.jrxml").getInputStream();
	            JasperDesign design = JRXmlLoader.load(resource);
	            JasperReport jsprep = JasperCompileManager.compileReport(design);
	            JasperPrint print = JasperFillManager.fillReport(jsprep, params, new JREmptyDataSource());
	            JasperExportManager.exportReportToPdfStream(print, baos);
	      

	        baos.close();
	        ByteArrayInputStream inStream = new ByteArrayInputStream(baos.toByteArray());
	        exportedItem.put("file", Base64.getEncoder().encodeToString(baos.toByteArray()));

	        InputStreamResource res = new InputStreamResource(inStream);
	        return exportedItem;

	    }
}
