package org.davisr.spring.camel.fmd.nmvs.response;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import org.apache.cxf.message.MessageContentsList;
import org.davisr.spring.camel.fmd.bag.model.Pack;
import org.davisr.spring.camel.nmvs.G110Response;
import org.davisr.spring.camel.nmvs.ResponsePackType;
import org.davisr.spring.camel.nmvs.ResponseProductType;
import org.springframework.stereotype.Component;

@Component("fmdResponseBuilder")
public class SinglePackResponseBuilder {

	public FMDResponse buildG110Response (G110Response response) {
		FMDResponse r = FMDResponse.builder()
		.code(response.getBody().getReturnCode().getCode().name())
		.description(response.getBody().getReturnCode().getDesc())
		.build();
		ResponsePackType nmvsPack = response.getBody().getPack();
		ResponseProductType nmvsProduct = response.getBody().getProduct();
		if (nmvsPack != null) {
			r.setPack(Pack.builder()
						.batch(nmvsProduct.getBatch().getId())
						.expiry(buildDate(nmvsProduct.getBatch().getExpDate()))
						.gtin(nmvsProduct.getProductCode().getValue())
						.serialNumber(nmvsPack.getSn())
						.packState(nmvsPack.getState().name())
						.build());
		}
		return r;
	}
	
	private Date buildDate(String date) {
		try {
			DateFormat df = new SimpleDateFormat("yyMMdd", Locale.ENGLISH);
			return df.parse(date);
		}
		catch (Exception e) {
			return null;
		}
	}
	public ArrayList<FMDResponse> buildBagVerifyResults(ArrayList responses){
		ArrayList<FMDResponse> fmdResponses = new ArrayList<FMDResponse>();
		ArrayList<MessageContentsList> contents = (ArrayList<MessageContentsList>) responses;
		contents.forEach(contentList -> {
			G110Response g110Response = (G110Response) contentList.get(0);
			fmdResponses.add(buildG110Response(g110Response));
		});

		return fmdResponses;
	}
}
