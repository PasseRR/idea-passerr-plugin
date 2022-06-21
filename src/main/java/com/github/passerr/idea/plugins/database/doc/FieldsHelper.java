package com.github.passerr.idea.plugins.database.doc;

import com.intellij.database.model.DasColumn;
import com.intellij.database.model.DasObject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import javax.swing.JCheckBox;
import java.awt.event.ItemEvent;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 字段工具
 * @author xiehai
 * @date 2022/06/21 13:13
 */
class FieldsHelper {
    /**
     * 表字段checkbox
     * @param value 初始值
     * @return {@link List}
     */
    static List<JCheckBox> tableFields(AtomicInteger value) {
        return FieldsHelper.fields(TableFields.class, value);
    }

    /**
     * 列字段checkbox
     * @param value 初始值
     * @return {@link List}
     */
    static List<JCheckBox> columnFields(AtomicInteger value) {
        return FieldsHelper.fields(ColumnFields.class, value);
    }

    /**
     * 枚举转checkbox
     * @param clazz 枚举类型
     * @param value 初始值
     * @param <T>   枚举类型
     * @return {@link List}
     */
    private static <T extends Enum<?> & Checkable<?>> List<JCheckBox> fields(Class<T> clazz, AtomicInteger value) {
        return
            Arrays.stream(clazz.getEnumConstants())
                .map(it -> {
                    JCheckBox checkBox = new JCheckBox();
                    checkBox.setSelected(it.isSelected());
                    if (it.isSelected()) {
                        value.set(value.get() | it.getBit());
                    }
                    checkBox.setEnabled(it.isEnable());
                    checkBox.setText(it.getLabel());
                    checkBox.addItemListener(e -> {
                        if (e.getStateChange() == ItemEvent.SELECTED) {
                            value.set(value.get() | it.getBit());
                        } else if (e.getStateChange() == ItemEvent.DESELECTED) {
                            value.set(~value.get() & it.getBit());
                        }
                    });

                    return checkBox;
                })
                .collect(Collectors.toList());
    }

    @RequiredArgsConstructor
    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    enum TableFields implements Checkable<DasObject> {
        /**
         * 序号
         */
        SEQ("序号", false, 1, true) {
            @Override
            public String value(DasObject dasObject) {
                return String.format("%d. ", INDEX.get().getAndIncrement());
            }
        },
        NAME("名称", true, 1 << 1, false) {
            @Override
            public String value(DasObject dasObject) {
                return dasObject.getName();
            }
        },
        COMMENT("注释", true, 1 << 2, true) {
            @Override
            public String value(DasObject dasObject) {
                return
                    Optional.ofNullable(dasObject.getComment())
                        .map(it -> String.format("(%s)", it))
                        .orElse("");
            }
        };
        String label;
        boolean selected;
        int bit;
        boolean enable;
        private static final ThreadLocal<AtomicInteger> INDEX = ThreadLocal.withInitial(AtomicInteger::new);

        static void reset() {
            INDEX.get().set(1);
        }

        static void remove() {
            INDEX.remove();
        }
    }

    @RequiredArgsConstructor
    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    enum ColumnFields implements Checkable<DasColumn> {
        /**
         * 序号
         */
        SEQ("序号", false, 1, true) {
            @Override
            public String value(DasColumn dasColumn) {
                return String.valueOf(INDEX.get().getAndIncrement());
            }
        },
        NAME("名称", true, 1 << 1, false) {
            @Override
            public String value(DasColumn dasColumn) {
                return dasColumn.getName();
            }
        },
        TYPE("类型", true, 1 << 2, false) {
            @Override
            public String value(DasColumn dasColumn) {
                return dasColumn.getDataType().getSpecification();
            }
        },
        PRIMARY("是否主键", false, 1 << 3, true) {
            @Override
            public String value(DasColumn dasColumn) {
                return "否";
            }
        },
        NULLABLE("是否可为空", false, 1 << 4, true) {
            @Override
            public String value(DasColumn dasColumn) {
                return dasColumn.isNotNull() ? "否" : "是";
            }
        },
        DEFAULT("默认值", false, 1 << 5, true) {
            @Override
            public String value(DasColumn dasColumn) {
                return Optional.ofNullable(dasColumn.getDefault()).orElse("");
            }
        },
        COMMENT("注释", true, 1 << 6, true) {
            @Override
            public String value(DasColumn dasColumn) {
                return Optional.ofNullable(dasColumn.getComment()).orElse("");
            }
        };
        String label;
        boolean selected;
        int bit;
        boolean enable;
        private static final ThreadLocal<AtomicInteger> INDEX = ThreadLocal.withInitial(AtomicInteger::new);

        static void reset() {
            INDEX.get().set(1);
        }

        static void remove() {
            INDEX.remove();
        }
    }

    private interface Checkable<T> {
        String getLabel();
        boolean isSelected();
        int getBit();
        boolean isEnable();
        String value(T t);
    }
}
