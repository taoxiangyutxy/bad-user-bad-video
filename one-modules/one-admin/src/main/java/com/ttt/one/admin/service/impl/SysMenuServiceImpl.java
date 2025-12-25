package com.ttt.one.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ttt.one.admin.dao.SysMenuMapper;
import com.ttt.one.admin.entity.SysMenu;
import com.ttt.one.admin.service.SysMenuService;
import com.ttt.one.admin.vo.SysMenuVO;
import com.ttt.one.common.utils.Constant;
import com.ttt.one.common.utils.PageUtils;
import com.ttt.one.common.utils.Query;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service("sysMenuService")
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {
    @Autowired
    private SysMenuMapper sysMenuMapper;
    @Override
    public PageUtils queryPageAll(Map<String, Object> params) {
        QueryWrapper<SysMenu> wrapper = new QueryWrapper<>();

        wrapper.orderByDesc("create_time");

        if (params.containsKey("menuName") && StringUtils.isNotBlank((String) params.get("menuName"))) {
            wrapper.like("menu_name", (String) params.get("menuName"));
        }

        if (params.containsKey("parentId") && StringUtils.isNotBlank((String) params.get("parentId"))) {
            wrapper.eq("parent_id", (String) params.get("parentId"));
        }

        IPage<SysMenu> page = this.page(
                new Query<SysMenu>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public SysMenu getSysMenuById(Long id) {
        return this.baseMapper.selectById(id);
    }

    @Override
    public SysMenu createSysMenu(SysMenuVO menu) {
        SysMenu menuDb = new SysMenu();
        BeanUtils.copyProperties(menu, menuDb);
        if(menuDb.getParentId() == null){
            menuDb.setParentId(0L);
        }
        if(menuDb.getParentId() == Constant.IS_VISIBLE_0){
            menuDb.setType(Constant.IS_VISIBLE_0);
        }
        menuDb.setIsVisible(Constant.IS_VISIBLE_1);
        this.baseMapper.insert(menuDb);
        return menuDb;
    }

    @Override
    public SysMenu updateSysMenu(SysMenuVO menu) {
        SysMenu menuDb = new SysMenu();
        BeanUtils.copyProperties(menu, menuDb);
        if(menuDb.getParentId() == 0){
            menuDb.setType(0);
        }
        this.baseMapper.updateById(menuDb);
        return menuDb;
    }

    @Override
    public void deleteSysMenu(Long[] ids) {
        for (Long id : ids) {
            // 递归删除菜单及其所有子菜单
            deleteMenuRecursively(id);
        }
        this.baseMapper.deleteBatchIds(Arrays.asList(ids));
    }

    /**
     * 递归删除菜单及其子菜单
     * @param id 菜单ID
     */
    private void deleteMenuRecursively(Long id) {
        // 先删除所有子菜单
        List<SysMenu> childMenus = this.baseMapper.selectList(
                new QueryWrapper<SysMenu>().eq("parent_id", id)
        );

        for (SysMenu childMenu : childMenus) {
            deleteMenuRecursively(childMenu.getMenuId());
        }

        // 删除当前菜单
        this.baseMapper.deleteById(id);
    }

    @Override
    public List<SysMenu> selectMenusByUserId(Long userId) {
        return this.baseMapper.selectMenusByUserId(userId,null, Constant.IS_VISIBLE_1);
    }

    @Override
    public List<SysMenu> selectMenuTree(Map<String, Object> condition) {
        return this.baseMapper.selectMenuTree(condition);
    }

    @Override
    public List<SysMenu> findByRoleId(Long roleId) {
        return this.baseMapper.findByRoleId(roleId);
    }

    @Override
    public List<SysMenu> findByParentId(Long menuId) {
        QueryWrapper<SysMenu> wrapper = new QueryWrapper<>();
        wrapper.eq("parent_id", menuId);
        return this.baseMapper.selectList(wrapper);
    }

    @Override
    public List<String> getRoleNamesByMenuId(Long id) {
        return sysMenuMapper.getRoleNamesByMenuId(id);
    }
}
