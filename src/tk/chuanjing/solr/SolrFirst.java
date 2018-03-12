package tk.chuanjing.solr;

import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

/**
 * 演示solr的增删改查，使用solrj
 * 
 * @author ChuanJing
 * @date 2017年12月31日 上午11:08:52
 * @version 1.0
 */
public class SolrFirst {
	
	/**
	 * 向索引库添加文档
	 * @throws Exception
	 */
	@Test
	public void testAddDocument() throws Exception {
		//创建一个SolrServer对象，创建连接
		//参数：solr服务的地址
		//http://localhost:8080/solr	：默认为collection1
		//http://localhost:8080/solr/collection2 ：连接collection2
		SolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr");
		
		//创建一个文档对象
		SolrInputDocument document = new SolrInputDocument();
		
		//向文档中添加域
		//每个文档必须有id域，而且其他域必须在schema.xml中定义
		document.addField("id", "test001");
		document.addField("product_name", "测试商品");
		document.addField("product_price", "100");
		
		//把文档添加到索引库
		solrServer.add(document);
		
		//提交
		solrServer.commit();
	}
	
	/**
	 * 根据id删除
	 * @throws Exception
	 */
	@Test
	public void testDeleteDocumentById() throws Exception {
		SolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr");
		//根据id删除文档
		solrServer.deleteById("test001");
		//提交
		solrServer.commit();
	}
	
	/**
	 * 根据查询删除
	 * @throws Exception
	 */
	@Test
	public void testDeleteDocumentByQuery() throws Exception {
		SolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr");
		//参数：查询语法
		solrServer.deleteByQuery("*:*");// 删除所有
		//提交
		solrServer.commit();
	}
	
	/**
	 * 简单查询
	 * @throws Exception
	 */
	@Test
	public void testQueryIndex() throws Exception {
		SolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr");
		//创建一个SolrQuery对象
		SolrQuery query = new SolrQuery();
		//设置查询条件
//		query.set("q", "*:*");
		query.setQuery("*:*");
		//执行查询
		QueryResponse response = solrServer.query(query);
		//取查询结果
		SolrDocumentList solrDocumentList = response.getResults();
		System.out.println("查询结果总记录数：" + solrDocumentList.getNumFound());
		//遍历查询结果
		for (SolrDocument solrDocument : solrDocumentList) {
			System.out.println(solrDocument.get("id"));
			System.out.println(solrDocument.get("product_name"));
			System.out.println(solrDocument.get("product_price"));
			System.out.println(solrDocument.get("product_catalog_name"));
			System.out.println(solrDocument.get("product_picture"));
		}
	}
	
	/**
	 * 复杂查询
	 * @throws Exception
	 */
	@Test
	public void testQueryIndexFuza() throws Exception {
		SolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr");
		
		//创建一个查询对象
		SolrQuery query = new SolrQuery();
		
		//设置主查询条件
		query.setQuery("厨房");
		//设置过滤条件
		query.addFilterQuery("product_price:[0 TO 20]");
		//排序，参数1：要排序的域， 参数2：排序方式
		query.setSort("product_price", ORDER.asc);
		//设置分页
		query.setStart(0);
		query.setRows(5);
		//设置返回结果包含的域
		query.setFields("id", "product_name", "product_price", "product_catalog_name", "product_picture");
		//设置默认搜索域
		query.set("df", "product_keywords");
		//开启高亮
		query.setHighlight(true);
		//高亮显示的域
		query.addHighlightField("product_name");
		//高亮前缀
		query.setHighlightSimplePre("<em>");
		//高亮后缀
		query.setHighlightSimplePost("</em>");
		
		//执行查询
		QueryResponse response = solrServer.query(query);
		
		//取查询结果
		SolrDocumentList solrDocumentList = response.getResults();
		System.out.println("查询结果总记录数：" + solrDocumentList.getNumFound());
		//遍历查询结果
		for (SolrDocument solrDocument : solrDocumentList) {
			System.out.println(solrDocument.get("id"));
			
			//取高亮显示
			Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();
			// 最好在这判断下highlighting是否为null，因为如果没有打开高亮，那么highlighting会是null
			List<String> list = highlighting.get(solrDocument.get("id")).get("product_name");
			String productName = "";
			if (list != null && list.size() > 0) {
				// 如果有高亮就显示高亮
				productName = list.get(0);
			} else {
				// 没有高亮就显示普通
				productName = (String) solrDocument.get("product_name");
			}
			
			System.out.println(productName);
			System.out.println(solrDocument.get("product_price"));
			System.out.println(solrDocument.get("product_catalog_name"));
			System.out.println(solrDocument.get("product_picture"));
		}
	}
}