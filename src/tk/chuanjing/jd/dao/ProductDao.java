package tk.chuanjing.jd.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import tk.chuanjing.jd.pojo.ProductModel;
import tk.chuanjing.jd.pojo.ResultModel;

/**
 * @author ChuanJing
 * @date 2017年12月31日 下午3:04:08
 * @version 1.0
 */
@Repository
public class ProductDao {

	@Autowired
	private SolrServer solrServer;
	
//	public ResultModel queryProduct(SolrQuery query) throws Exception{
	public ResultModel search(SolrQuery query) throws Exception {
		//执行查询
		QueryResponse response = solrServer.query(query);
		//取查询结果
		SolrDocumentList solrDocumentList = response.getResults();
		
		ResultModel resultModel = new ResultModel();
		
		//取查询结果总记录数
		resultModel.setRecordCount(solrDocumentList.getNumFound());
		//商品列表
		List<ProductModel> productList = new ArrayList<ProductModel>();
		//取结果集
		for (SolrDocument solrDocument : solrDocumentList) {
			//创建一个商品对象
			ProductModel productModel = new ProductModel();
			
			productModel.setPid((String)solrDocument.get("id"));
			productModel.setCatalog_name((String)solrDocument.get("product_catalog_name"));
		
			//取高亮显示
			Map<String, Map<String, List<String>>> highlighting = response.getHighlighting();
			List<String> list = highlighting.get(solrDocument.get("id")).get("product_name");
			String productName = "";
			if (list != null && list.size() > 0) {
				productName = list.get(0);
			} else {
				productName = (String) solrDocument.get("product_name");
			}
			
			productModel.setName(productName);
			productModel.setPicture((String)solrDocument.get("product_picture"));
			productModel.setPrice((float)solrDocument.get("product_price"));
			
			//添加到商品列表
			productList.add(productModel);
		}
		//添加到返回结果
		resultModel.setProductList(productList);
		
		return resultModel;
	}
}
