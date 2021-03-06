package org.davisr.spring.camel.fmd.nmvs.request;

import java.text.SimpleDateFormat;
import java.util.List;

import org.davisr.spring.camel.fmd.bag.model.Bag;
import org.davisr.spring.camel.fmd.bag.model.Pack;
import org.davisr.spring.camel.nmvs.BaseBatchType;
import org.davisr.spring.camel.nmvs.CatalogProductSchemeType;
import org.davisr.spring.camel.nmvs.G110Request;
import org.davisr.spring.camel.nmvs.G120Request;
import org.davisr.spring.camel.nmvs.G121Request;
import org.davisr.spring.camel.nmvs.G180Request;
import org.davisr.spring.camel.nmvs.ProductIdentifierType;
import org.davisr.spring.camel.nmvs.RequestAuthHeaderDataType;
import org.davisr.spring.camel.nmvs.RequestDataType;
import org.davisr.spring.camel.nmvs.RequestHeaderDataType;
import org.davisr.spring.camel.nmvs.RequestPackType;
import org.davisr.spring.camel.nmvs.RequestProductType;
import org.davisr.spring.camel.nmvs.RequestTransactionHeaderDataType;
import org.davisr.spring.camel.nmvs.RequestUndoSingleDataType;
import org.davisr.spring.camel.nmvs.UserSoftwareType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("singlePackRequestBuilder")
public class SinglePackRequestBuilder {

    @Value("${nmvs.user}")
    String nmvsUser;
    @Value("${nmvs.password}")
    String nmvsPassword;

	public boolean isVerifyRequest (FMDRequest request) {
		return request.getOperation().equals("verify");
	}
	
	public boolean isDispenseRequest (FMDRequest request) {
		return request.getOperation().equals("dispense");
	}

	public boolean isUndoDispenseRequest (FMDRequest request) {
		return request.getOperation().equals("undo-dispense");
	}

	public boolean isStolenRequest (FMDRequest request) {
		return request.getOperation().equals("stolen");
	}

	public G110Request buildG110Request (Pack p) {
		G110Request r = new G110Request();
		r.setHeader(buildHeader());
		r.setBody(buildBody(p));
		return r;
	}
	
	public G120Request buildG120Request (Pack p) {
		G120Request r = new G120Request();
		r.setHeader(buildHeader());
		r.setBody(buildBody(p));
		return r;
	}

	public G121Request buildG121Request (Pack p) {
		G121Request r = new G121Request();
		r.setHeader(buildHeader());
		r.setBody(buildUndoBody(p));
		return r;
	}

	// NOTE G181 Undo Stolen is no longer allowed by NMVS
	// You will not be able to undo a pack whose state
	// has been set to STOLEN
	public G180Request buildG180Request (Pack p) {
		G180Request r = new G180Request();
		r.setHeader(buildHeader());
		r.setBody(buildBody(p));
		return r;
	}

	public Bag getBag (FMDRequest request) {
		return request.getBag();
	}

	public List<Pack> getPacksFromBag (Bag b) {
		return b.getPacks();
	}

	private RequestHeaderDataType buildHeader() {
		RequestHeaderDataType h = new RequestHeaderDataType();
		h.setAuth(buildAuthHeader());
		h.setTransaction(buildTransactionHeader());
		h.setUserSoftware(buildSoftwareHeader());
		return h;
	}
	
	private RequestAuthHeaderDataType buildAuthHeader () {
		RequestAuthHeaderDataType h = new RequestAuthHeaderDataType();
		h.setClientLoginId("SWS");
		h.setPassword(nmvsPassword);
		h.setUserId(nmvsUser);
		return h;
	}
	
	private RequestTransactionHeaderDataType buildTransactionHeader () {
		RequestTransactionHeaderDataType h = new RequestTransactionHeaderDataType();
		h.setLanguage("eng");
		h.setClientTrxId("Test-Transaction");
		return h;
	}

	private UserSoftwareType buildSoftwareHeader () {
		UserSoftwareType h = new UserSoftwareType();
		h.setName("Columbus");
		h.setSupplier("Boots");
		h.setVersion("1.0");
		return h;
	}
	
	private RequestDataType buildBody(Pack p) {
		RequestDataType r = new RequestDataType();
		r.setPack(buildPack(p));
		r.setProduct(buildProduct(p));
		return r;
	}
	
	private RequestUndoSingleDataType buildUndoBody(Pack p) {
		RequestUndoSingleDataType r = new RequestUndoSingleDataType();
		r.setPack(buildPack(p));
		r.setProduct(buildProduct(p));
		return r;
	}
	private RequestPackType buildPack (Pack p) {
		RequestPackType rp = new RequestPackType();
		rp.setSn(p.getSerialNumber());
		return rp;
	}

	private RequestProductType buildProduct (Pack p) {
		RequestProductType rp = new RequestProductType();
		rp.setBatch(buildBatch(p));
		rp.setProductCode(buildProductCode(p));
		return rp;
	}
	
	private BaseBatchType buildBatch (Pack p) {
		BaseBatchType b = new BaseBatchType();
		SimpleDateFormat f = new SimpleDateFormat("yyMMdd");
		b.setExpDate(f.format(p.getExpiry()));
		b.setId(p.getBatch());
		return b;
	}

	private ProductIdentifierType buildProductCode (Pack p) {
		ProductIdentifierType pi = new ProductIdentifierType();
		pi.setScheme(CatalogProductSchemeType.GTIN);
		pi.setValue(p.getGtin());
		return pi;
	}
	
	
}
