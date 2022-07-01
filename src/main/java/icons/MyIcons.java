package icons;

import com.intellij.openapi.util.IconLoader;

import javax.swing.Icon;

/**
 * 图标
 * @author xiehai
 * @date 2022/07/01 11:31
 */
public interface MyIcons {
    Icon TOOL_ICON = IconLoader.getIcon("/icons/plugin.svg", MyIcons.class);
}
