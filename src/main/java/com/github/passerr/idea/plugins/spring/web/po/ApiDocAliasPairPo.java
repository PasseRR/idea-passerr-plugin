package com.github.passerr.idea.plugins.spring.web.po;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * 别名对po
 * @author xiehai
 * @date 2021/06/30 19:29
 * @Copyright(c) tellyes tech. inc. co.,ltd
 */
@Data
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class ApiDocAliasPairPo {
    String type;
    String alias;

    public ApiDocAliasPairPo deepCopy() {
        return new ApiDocAliasPairPo(this.type, this.alias);
    }
}
