package org.davisr.spring.camel.fmd.nmvs.response;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.cxf.message.MessageContentsList;
import org.davisr.spring.camel.fmd.bag.model.Pack;
import org.davisr.spring.camel.fmd.nmvs.request.FMDRequest;
import org.davisr.spring.camel.nmvs.G110Response;
import org.davisr.spring.camel.nmvs.G120Response;
import org.davisr.spring.camel.nmvs.G121Response;
import org.davisr.spring.camel.nmvs.G180Response;
import org.davisr.spring.camel.nmvs.G181Response;
import org.davisr.spring.camel.nmvs.O1BodyType;
import org.davisr.spring.camel.nmvs.ResponsePackType;
import org.davisr.spring.camel.nmvs.ResponseProductType;
import org.springframework.stereotype.Component;


@Component("fmdResponseBuilder")
public class SinglePackResponseBuilder {

	public FMDResponse buildG110Response (G110Response response) {
		return buildFMDResponse(response.getBody());
	}

	public FMDResponse buildG120Response (G120Response response) {
		return buildFMDResponse(response.getBody());
	}

	public FMDResponse buildG121Response (G121Response response) {
		return buildFMDResponse(response.getBody());
	}

	public FMDResponse buildG180Response (G180Response response) {
		return buildFMDResponse(response.getBody());
	}

	private FMDResponse buildFMDResponse (O1BodyType response) {
		FMDResponse r = FMDResponse.builder()
				.code(response.getReturnCode().getCode().name())
				.description(response.getReturnCode().getDesc())
			.build();
		ResponsePackType nmvsPack = response.getPack();
		ResponseProductType nmvsProduct = response.getProduct();
		List<String> reasons = new ArrayList<String>();
		r.setReasons(reasons);
		response.getPack().getReason().forEach(reason -> reasons.add(reason.name()));
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
	
	public ArrayList<FMDResponse> buildBagVerifyResults(ArrayList<?> responses, String requestOperation){
		ArrayList<FMDResponse> fmdResponses = new ArrayList<FMDResponse>();
		ArrayList<MessageContentsList> contents = (ArrayList<MessageContentsList>) responses;
		if (requestOperation.equals("verify")) {
			contents.forEach(l -> fmdResponses.add(buildG110Response((G110Response)l.get(0))));
		} else if (requestOperation.equals("dispense")) {
			contents.forEach(l -> fmdResponses.add(buildG120Response((G120Response)l.get(0))));
		} else if (requestOperation.equals("undo-dispense")) {
			contents.forEach(l -> fmdResponses.add(buildG121Response((G121Response)l.get(0))));
		} else if (requestOperation.equals("stolen")) {
			contents.forEach(l -> fmdResponses.add(buildG180Response((G180Response)l.get(0))));
		}
		
		return fmdResponses;
	}

	public FMDBagResponse buildBagResponse(ArrayList responses, FMDRequest request) {
		return FMDBagResponse.builder()
					.bagId(request.getBag().getId())
					.bagLabel(request.getBag().getLabelCode())
					.packResponses(buildBagVerifyResults(responses, request.getOperation()))
				.build();
	}
}
