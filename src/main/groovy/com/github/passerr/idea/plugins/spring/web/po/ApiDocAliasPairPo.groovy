package com.github.passerr.idea.plugins.spring.web.po


import com.intellij.util.xmlb.annotations.Property

/**
 * 别名对po
 * @date 2021/06/29 15:37
 * @Copyright (c) wisewe co.,ltd
 * @author xiehai
 */
class ApiDocAliasPairPo {
    @Property
    String type
    @Property
    String alias

    static ApiDocAliasPairPo deepCopy(ApiDocAliasPairPo source) {
        new ApiDocAliasPairPo(
            type: source.type,
            alias: source.alias
        )
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (getClass() != o.class) return false

        ApiDocAliasPairPo that = (ApiDocAliasPairPo) o

        if (alias != that.alias) return false
        if (type != that.type) return false

        return true
    }

    int hashCode() {
        int result
        result = (type != null ? type.hashCode() : 0)
        result = 31 * result + (alias != null ? alias.hashCode() : 0)
        return result
    }
}
