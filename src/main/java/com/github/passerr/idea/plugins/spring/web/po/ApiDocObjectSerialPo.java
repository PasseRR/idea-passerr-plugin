package com.github.passerr.idea.plugins.spring.web.po;

import com.github.passerr.idea.plugins.spring.web.WebCopyConstants;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 对象序列化
 * @author xiehai
 * @date 2021/06/30 19:29
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
@Data
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class ApiDocObjectSerialPo {
    /**
     * 类型全名
     */
    String type;
    /**
     * 别名
     */
    String alias;
    /**
     * 默认值
     */
    String value;

    public ApiDocObjectSerialPo deepCopy() {
        return new ApiDocObjectSerialPo(this.type, this.alias, this.value);
    }

    /**
     * 默认值设置
     * @return {@link List}
     */
    static List<ApiDocObjectSerialPo> defaultObjects() {
        List<ApiDocObjectSerialPo> objects = new ArrayList<>();
        WebCopyConstants.PRIMITIVE_SERIALS.stream().map(ApiDocObjectSerialPo::deepCopy).forEach(objects::add);
        WebCopyConstants.WRAPPED_SERIALS.stream().map(ApiDocObjectSerialPo::deepCopy).forEach(objects::add);

        return objects;
    }

    public boolean isOk() {
        return
            Objects.nonNull(this.type) && !this.type.isEmpty()
                && Objects.nonNull(this.alias) && !this.alias.isEmpty()
                && Objects.nonNull(this.value) && !this.value.isEmpty();
    }
}
