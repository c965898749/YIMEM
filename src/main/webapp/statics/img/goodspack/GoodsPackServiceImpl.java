package org.slsale.service.goodspack;

import java.util.List;

import javax.annotation.Resource;

import org.slsale.dao.goodspack.GoodsPackMapper;
import org.slsale.dao.goodspackaffiliated.GoodsPackAffiliatedMapper;
import org.slsale.pojo.GoodsPack;
import org.slsale.pojo.GoodsPackAffiliated;
import org.springframework.stereotype.Service;
@Service
public class GoodsPackServiceImpl implements GoodsPackService{
	@Resource
	private GoodsPackMapper mapper;
	@Resource
	private GoodsPackAffiliatedMapper gpaMapper;

	public List<GoodsPack> getGoodsPackList(GoodsPack goodsPack)
			throws Exception {
		// TODO Auto-generated method stub
		return mapper.getGoodsPackList(goodsPack);
	}

	public int count(GoodsPack goodsPack) throws Exception {
		// TODO Auto-generated method stub
		return mapper.count(goodsPack);
	}

	public int addGoodsPack(GoodsPack goodsPack) throws Exception {
		// TODO Auto-generated method stub
		return mapper.addGoodsPack(goodsPack);
	}

	public int goodsPackCodeIsExit(GoodsPack goodsPack) throws Exception {
		// TODO Auto-generated method stub
		return mapper.goodsPackCodeIsExit(goodsPack);
	}

	public GoodsPack getGoodsPackById(GoodsPack goodsPack) throws Exception {
		// TODO Auto-generated method stub
		return mapper.getGoodsPackById(goodsPack);
	}

	public int modifyGoodsPack(GoodsPack goodsPack) {
		// TODO Auto-generated method stub
		return mapper.modifyGoodsPack(goodsPack);
	}

	public int deleteGoodsPack(GoodsPack goodsPack) {
		// TODO Auto-generated method stub
		return mapper.deleteGoodsPack(goodsPack);
	}

	public boolean hl_addGoodsPack(GoodsPack goodsPack,
			List<GoodsPackAffiliated> apaList) throws Exception {
		// TODO Auto-generated method stub
		mapper.addGoodsPack(goodsPack);
		
		
		
		return false;
	}


}
