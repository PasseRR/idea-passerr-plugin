package com.github.passerr.idea.plugins.database.doc;

import cn.wisewe.docx4j.output.builder.document.DocumentExporter;
import cn.wisewe.docx4j.output.builder.document.ParagraphStyle;
import com.intellij.database.model.DasColumn;
import com.intellij.database.model.DasObject;
import com.intellij.database.model.ObjectKind;
import com.intellij.database.util.DasUtil;
import com.intellij.util.containers.JBIterable;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文档导出
 * @author xiehai
 * @date 2022/06/21 13:00
 */
class DocxExporter {
    static void export(List<DasObject> schemas, String title, int tableFields, int columnFields, String path) {
        try {
            List<FieldsHelper.TableFields> tableFieldList =
                Arrays.stream(FieldsHelper.TableFields.values())
                    .filter(it -> (it.getBit() & tableFields) > 0)
                    .collect(Collectors.toList());
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
        List<String> columnHeaders =
            columnFields.stream()
                .map(FieldsHelper.ColumnFields::getLabel)
                .collect(Collectors.toList());
        schemas.forEach(s -> {
            FieldsHelper.TableFields.reset();
            String fileName = s.getName();
            DocumentExporter exporter = DocumentExporter.create()
                .headingParagraph(title, ParagraphStyle.HEADING_1);
            
            s.getDasChildren(ObjectKind.TABLE)
                .forEach(t -> {
                    exporter.headingParagraph(
                        tableFields.stream().map(it -> it.value(t)).collect(Collectors.joining()),
                        ParagraphStyle.HEADING_2
                    );
                    
                    JBIterable<? extends DasColumn> dasColumns = DasUtil.getColumns(t);
                    int rows = dasColumns.size();
                    exporter.table(rows + 1, columns, tb -> {
                        FieldsHelper.ColumnFields.reset();
                        tb.row(r -> r.headCells(columnHeaders))
                            .rows(dasColumns, (c, r) ->
                                r.dataCells(columnFields.stream().map(it -> it.value(c)).collect(Collectors.toList()))
                            );
                    });
                });

            try {
                exporter.writeTo(
                    new FileOutputStream(Paths.get(path, exporter.defaultFileType().fullName(fileName)).toString())
                );
            } catch (FileNotFoundException ignore) {
            }
        });
    }
}
