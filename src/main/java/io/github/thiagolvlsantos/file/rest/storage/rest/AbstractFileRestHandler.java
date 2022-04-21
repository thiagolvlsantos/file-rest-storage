package io.github.thiagolvlsantos.file.rest.storage.rest;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.thiagolvlsantos.file.rest.storage.service.AbstractFileService;
import io.github.thiagolvlsantos.file.storage.KeyParams;
import io.github.thiagolvlsantos.file.storage.annotations.UtilAnnotations;
import io.github.thiagolvlsantos.file.storage.exceptions.FileStorageException;
import io.github.thiagolvlsantos.file.storage.resource.Resource;
import io.github.thiagolvlsantos.rest.storage.rest.AbstractRestHandler;
import io.github.thiagolvlsantos.rest.storage.rest.WrapperVO;
import io.github.thiagolvlsantos.rest.storage.rest.basic.RestCountEvent;
import io.github.thiagolvlsantos.rest.storage.rest.basic.RestDeleteEvent;
import io.github.thiagolvlsantos.rest.storage.rest.basic.RestListEvent;
import io.github.thiagolvlsantos.rest.storage.rest.basic.RestReadEvent;
import io.github.thiagolvlsantos.rest.storage.rest.basic.RestSaveEvent;
import io.github.thiagolvlsantos.rest.storage.rest.basic.RestUpdateEvent;
import io.github.thiagolvlsantos.rest.storage.rest.history.HistoryVO;
import io.github.thiagolvlsantos.rest.storage.rest.history.RestHistoryEvent;
import io.github.thiagolvlsantos.rest.storage.rest.history.RestHistoryNameEvent;
import io.github.thiagolvlsantos.rest.storage.rest.history.RestHistoryResourceEvent;
import io.github.thiagolvlsantos.rest.storage.rest.properties.RestGetPropertyEvent;
import io.github.thiagolvlsantos.rest.storage.rest.properties.RestListPropertiesEvent;
import io.github.thiagolvlsantos.rest.storage.rest.properties.RestPropertiesEvent;
import io.github.thiagolvlsantos.rest.storage.rest.properties.RestSetPropertiesEvent;
import io.github.thiagolvlsantos.rest.storage.rest.properties.RestSetPropertyEvent;
import io.github.thiagolvlsantos.rest.storage.rest.resources.ResourceVO;
import io.github.thiagolvlsantos.rest.storage.rest.resources.RestCountResourcesEvent;
import io.github.thiagolvlsantos.rest.storage.rest.resources.RestDeleteResourceEvent;
import io.github.thiagolvlsantos.rest.storage.rest.resources.RestGetResourceEvent;
import io.github.thiagolvlsantos.rest.storage.rest.resources.RestListResourcesEvent;
import io.github.thiagolvlsantos.rest.storage.rest.resources.RestSetResourceEvent;
import io.github.thiagolvlsantos.rest.storage.rest.resources.RestUpdateResourceEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

@Getter
@Setter
public abstract class AbstractFileRestHandler<P, Q> extends AbstractRestHandler<P> {

	protected Class<Q> typeAlias;
	protected @Autowired IObjectMapper objectMapper;
	protected @Autowired ObjectMapper mapper;
	protected @Autowired AbstractFileService<P> service;

	public AbstractFileRestHandler(String entity, Class<P> type, Class<Q> typeAlias) {
		super(entity, type);
		this.typeAlias = typeAlias;
	}

	@SneakyThrows
	public void save(RestSaveEvent<P> event) {
		event.setResult(service.save(toInstance(mapper.readValue(event.getContent(), typeAlias))));
	}

	protected abstract P toInstance(Q alias);

	@SneakyThrows
	public void read(RestReadEvent<P> event) {
		event.setResult(service.read(KeyParams.of(event.getName()), event.getCommit(), event.getAt()));
	}

	@SneakyThrows
	public void update(RestUpdateEvent<P> event) {
		P candidate = mapper.readValue(event.getContent(), type);
		String keys = UtilAnnotations.getKeysChain(type, candidate);
		String name = event.getName();
		if (!name.equalsIgnoreCase(keys)) {
			throw new FileStorageException(
					"Content name '" + keys + "' does not match the received path '" + name + "'.", null);
		}
		event.setResult(service.update(candidate));
	}

	@SneakyThrows
	public void delete(RestDeleteEvent<P> event) {
		event.setResult(service.delete(KeyParams.of(event.getName())));
	}

	@SneakyThrows
	public void setProperty(RestSetPropertyEvent<P> event) {
		event.setResult(
				service.setProperty(KeyParams.of(event.getName()), event.getProperty(), event.getDataAsString()));
	}

	@SneakyThrows
	public void setProperty(RestSetPropertiesEvent<List<P>> event) {
		event.setResult(service.setProperty(event.getProperty(), event.getDataAsString(), event.getFilter(),
				event.getPaging(), event.getSorting()));
	}

	@SneakyThrows
	public void getProperty(RestGetPropertyEvent<WrapperVO<Object>> event) {
		event.setResult(service.getProperty(KeyParams.of(event.getName()), event.getProperty(), event.getCommit(),
				event.getAt()));
	}

	@SneakyThrows
	public void properties(RestPropertiesEvent<Map<String, Object>> event) {
		event.setResult(service.properties(KeyParams.of(event.getName()), KeyParams.of(event.getProperties(), ","),
				event.getCommit(), event.getAt()));
	}

	@SneakyThrows
	public void setResource(RestSetResourceEvent<P> event) {
		Resource resource = objectMapper.map(event.getResource(), Resource.class);
		event.setResult(service.setResource(KeyParams.of(event.getName()), resource));
	}

	@SneakyThrows
	public void getResource(RestGetResourceEvent<ResourceVO> event) {
		Resource resource = service.getResource(KeyParams.of(event.getName()), event.getPath(), event.getCommit(),
				event.getAt());
		event.setResult(objectMapper.map(resource, ResourceVO.class));
	}

	@SneakyThrows
	public void updateResource(RestUpdateResourceEvent<P> event) {
		Resource resource = objectMapper.map(event.getResource(), Resource.class);
		event.setResult(service.updateResource(KeyParams.of(event.getName()), resource));
	}

	@SneakyThrows
	public void deleteResource(RestDeleteResourceEvent<P> event) {
		event.setResult(service.deleteResource(KeyParams.of(event.getName()), event.getPath()));
	}

	@SneakyThrows
	public void countResources(RestCountResourcesEvent<WrapperVO<Long>> event) {
		event.setResult(service.countResources(KeyParams.of(event.getName()), event.getFilter(), event.getPaging(),
				event.getCommit(), event.getAt()));
	}

	@SneakyThrows
	public void listResources(RestListResourcesEvent<List<ResourceVO>> event) {
		List<Resource> resources = service.listResources(KeyParams.of(event.getName()), event.getFilter(),
				event.getPaging(), event.getSorting(), event.getCommit(), event.getAt());
		event.setResult(objectMapper.mapList(resources, ResourceVO.class));
	}

	@SneakyThrows
	public void history(RestHistoryEvent<List<HistoryVO>> event) {
		event.setResult(service.history(KeyParams.of(new Object[0]), event.getPaging()));
	}

	@SneakyThrows
	public void historyName(RestHistoryNameEvent<List<HistoryVO>> event) {
		event.setResult(service.history(KeyParams.of(event.getName()), event.getPaging()));
	}

	@SneakyThrows
	public void historyResources(RestHistoryResourceEvent<List<HistoryVO>> event) {
		event.setResult(service.historyResources(KeyParams.of(event.getName()), event.getPath(), event.getPaging()));
	}

	@SneakyThrows
	public void count(RestCountEvent<WrapperVO<Long>> event) {
		event.setResult(service.count(event.getFilter(), event.getPaging(), event.getCommit(), event.getAt()));
	}

	@SneakyThrows
	public void list(RestListEvent<List<P>> event) {
		event.setResult(service.list(event.getFilter(), event.getPaging(), event.getSorting(), event.getCommit(),
				event.getAt()));
	}

	@SneakyThrows
	public void properties(RestListPropertiesEvent<Map<String, Map<String, Object>>> event) {
		event.setResult(service.properties(KeyParams.of(event.getProperties(), ","), event.getFilter(),
				event.getPaging(), event.getSorting(), event.getCommit(), event.getAt()));
	}
}