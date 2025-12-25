

package com.ttt.one.common.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页工具类
 *
 */
@Data
public class PageAdminUtils implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * 总记录数
	 */
	private int total;
	/**
	 * 每页记录数
	 */
	private int size;
	/**
	 * 总页数
	 */
	private int totalPage;
	/**
	 * 当前页数
	 */
	private int current;
	/**
	 * 列表数据
	 */
	private List<?> records;

	/**
	 * 分页
	 * @param list        列表数据
	 * @param totalCount  总记录数
	 * @param pageSize    每页记录数
	 * @param currPage    当前页数
	 */
	public PageAdminUtils(List<?> list, int totalCount, int pageSize, int currPage) {
		this.records = list;
		this.total = totalCount;
		this.size = pageSize;
		this.current = currPage;
		this.totalPage = (int)Math.ceil((double)totalCount/pageSize);
	}

	/**
	 * 分页
	 */
	public PageAdminUtils(IPage<?> page) {
		this.records = page.getRecords();
		this.total = (int)page.getTotal();
		this.size = (int)page.getSize();
		this.current = (int)page.getCurrent();
		this.totalPage = (int)page.getPages();
	}
}
