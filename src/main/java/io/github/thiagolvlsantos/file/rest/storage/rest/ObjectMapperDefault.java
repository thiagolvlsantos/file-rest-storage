package io.github.thiagolvlsantos.file.rest.storage.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.SneakyThrows;
import ma.glasnost.orika.MapperFacade;

@Component
public class ObjectMapperDefault implements IObjectMapper {
	private @Autowired ObjectMapper mapperJson;
	private @Autowired MapperFacade mapper;

	@Override
	@SneakyThrows
	public <Q> Q read(String content, Class<Q> type) {
		return mapperJson.readValue(content, type);
	}

	@Override
	public <P, Q> Q map(P source, Class<Q> type) {
		return mapper.map(source, type);
	}

	@Override
	public <P, Q> List<Q> mapList(Iterable<P> source, Class<Q> type) {
		return mapper.mapAsList(source, type);
	}
}
