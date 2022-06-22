package com.github.passerr.idea.plugins.database.doc;

import com.intellij.database.model.DasColumn;
import com.intellij.database.model.DasObject;
import com.intellij.database.model.ObjectKind;
import com.intellij.database.util.DasUtil;
import com.intellij.util.containers.JBIterable;
import com.lowagie.text.Cell;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Table;
import com.lowagie.text.rtf.RtfWriter2;
import com.lowagie.text.rtf.style.RtfParagraphStyle;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 文档导出
 * @author xiehai
 * @date 2022/06/21 13:00
 */
class DocExporter {
    static void export(List<DasObject> schemas, String title, int tableFields, int columnFields, String path) {
        try {
            // 选择的表字段
            List<FieldsHelper.TableFields> tableFieldList =
                Arrays.stream(FieldsHelper.TableFields.values())
                    .filter(it -> (it.getBit() & tableFields) > 0)
                    .collect(Collectors.toList());
            // 选择的列字段
            List<FieldsHelper.ColumnFields> columnFieldList =
                Arrays.stream(FieldsHelper.ColumnFields.values())
                    .filter(it -> (it.getBit() & columnFields) > 0)
                    .collect(Collectors.toList());
            doExport(schemas, title, tableFieldList, columnFieldList, path);
        } finally {
            FieldsHelper.TableFields.remove();
            FieldsHelper.ColumnFields.remove();
        }
    }

    private static void doExport(List<DasObject> schemas, String title, List<FieldsHelper.TableFields> tableFields,
                                 List<FieldsHelper.ColumnFields> columnFields, String path) {
        int columns = columnFields.size();
        List<Cell> columnHeaders =
            columnFields.stream()
                .map(FieldsHelper.ColumnFields::getLabel)
                .map(Cell::new)
                .peek(it -> it.setHeader(true))
                .collect(Collectors.toList());
        schemas.forEach(s -> {
            FieldsHelper.TableFields.reset();
            String fileName =
                Optional.ofNullable(s.getDasParent())
                    .map(it -> String.format("%s.", it.getName()))
                    .orElse("") + s.getName();
            Document document = new Document(PageSize.A4);
            try {
                RtfWriter2.getInstance(
                    document,
                    new FileOutputStream(Paths.get(path, String.format("%s.doc", fileName)).toString())
                );
                document.open();
                Paragraph header = new Paragraph(title, RtfParagraphStyle.STYLE_HEADING_1);
                header.setAlignment(Element.ALIGN_CENTER);
                document.add(header);
                JBIterable<? extends DasObject> dasTables = s.getDasChildren(ObjectKind.TABLE);
                for (DasObject t : dasTables) {
                    FieldsHelper.ColumnFields.reset();
                    Paragraph p = new Paragraph(
                        tableFields.stream().map(it -> it.value(t)).collect(Collectors.joining()),
                        RtfParagraphStyle.STYLE_HEADING_2
                    );
                    document.add(p);

                    Table table = new Table(columns);
                    table.setBorderWidth(1);
                    table.setWidth(98);
                    for (Cell cell : columnHeaders) {
                        table.addCell(cell);
                    }
                    JBIterable<? extends DasColumn> dasColumns = DasUtil.getColumns(t);
                    for (DasColumn c : dasColumns) {
                        List<Cell> cells = columnFields.stream().map(it -> it.value(c)).map(Cell::new).collect(
                            Collectors.toList());
                        for (Cell cell : cells) {
                            table.addCell(cell);
                        }
                    }
                    document.add(table);
                }
            } catch (FileNotFoundException | DocumentException ignore) {
            } finally {
                document.close();
            }
        });
    }
}
