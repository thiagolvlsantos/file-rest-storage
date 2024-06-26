package io.github.thiagolvlsantos.file.rest.storage.service;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;

import io.github.thiagolvlsantos.file.rest.storage.rest.IObjectMapper;
import io.github.thiagolvlsantos.file.storage.KeyParams;
import io.github.thiagolvlsantos.file.storage.exceptions.FileStorageException;
import io.github.thiagolvlsantos.file.storage.exceptions.FileStorageNotFoundException;
import io.github.thiagolvlsantos.file.storage.resource.Resource;
import io.github.thiagolvlsantos.file.storage.search.FilePaging;
import io.github.thiagolvlsantos.file.storage.util.repository.AbstractFileRepository;
import io.github.thiagolvlsantos.git.transactions.GitRepo;
import io.github.thiagolvlsantos.git.transactions.GitServices;
import io.github.thiagolvlsantos.git.transactions.exceptions.GitTransactionsException;
import io.github.thiagolvlsantos.git.transactions.read.GitCommit;
import io.github.thiagolvlsantos.git.transactions.read.GitRead;
import io.github.thiagolvlsantos.git.transactions.write.GitWrite;
import io.github.thiagolvlsantos.rest.storage.rest.WrapperVO;
import io.github.thiagolvlsantos.rest.storage.rest.history.HistoryVO;

public class AbstractFileService<T> {

	private @Autowired GitServices gits;
	private @Autowired IObjectMapper mapper;
	private @Autowired AbstractFileRepository<T> repository;

	public AbstractFileRepository<T> repository() {
		return repository;
	}

	// +---
	public String group() {
		GitRepo repo = AnnotationUtils.findAnnotation(getClass(), GitRepo.class);
		if (repo == null) {
			throw new GitTransactionsException("Repository location not found.", null);
		}
		return repo.value();
	}

	protected File readDirectory() {
		return gits.readDirectory(group());
	}

	protected File writeDirectory() {
		return gits.writeDirectory(group());
	}

	// +------------- ENTITY METHODS ------------------+

	@GitRead
	public boolean exists(T obj) {
		return repository().exists(readDirectory(), obj);
	}

	@GitWrite
	public T save(T obj) {
		try {
			beforeSave(obj);
			T result = repository().write(writeDirectory(), obj);
			afterSaveSuccess(obj, result);
			return result;
		} catch (Throwable e) {
			afterSaveError(obj, e);
			throw e;
		}
	}

	protected void beforeSave(T obj) {
		if (repository().exists(writeDirectory(), obj)) {
			throw new FileStorageException(repository().getType().getSimpleName() + " already exists.", null);
		}
	}

	protected void afterSaveSuccess(T obj, T result) {
		// default to nothing
	}

	protected void afterSaveError(T obj, Throwable e) {
		// default to nothing
	}

	@GitRead
	public T read(KeyParams keys, @GitCommit String commit, @GitCommit Long at) {
		try {
			beforeRead(keys, commit, at);
			T result = repository().read(readDirectory(), keys);
			afterReadSuccess(keys, commit, at, result);
			return result;
		} catch (Throwable e) {
			afterReadError(keys, commit, at, e);
			throw e;
		}
	}

	protected void beforeRead(KeyParams keys, String commit, Long at) {
		// default to nothing
	}

	protected void afterReadSuccess(KeyParams keys, String commit, Long at, T result) {
		// default to nothing
	}

	protected void afterReadError(KeyParams keys, String commit, Long at, Throwable e) {
		// default to nothing
	}

	@GitWrite
	public T update(T obj) {
		try {
			beforeUpdate(obj);
			T result = repository().write(writeDirectory(), obj);
			afterUpdateSuccess(obj, result);
			return result;
		} catch (Throwable e) {
			afterUpdateError(obj, e);
			throw e;
		}
	}

	protected void beforeUpdate(T obj) {
		if (!repository().exists(writeDirectory(), obj)) {
			throw new FileStorageNotFoundException(repository().getType().getSimpleName() + " not found.", null);
		}
	}

	protected void afterUpdateSuccess(T obj, T result) {
		// default to nothing
	}

	protected void afterUpdateError(T obj, Throwable e) {
		// default to nothing
	}

	@GitWrite
	public T delete(KeyParams keys) {
		try {
			beforeDelete(keys);
			T result = repository().delete(writeDirectory(), keys);
			afterDeleteSuccess(keys, result);
			return result;
		} catch (Throwable e) {
			afterDeleteError(keys, e);
			throw e;
		}
	}

	protected void beforeDelete(KeyParams keys) {
		// default to nothing
	}

	protected void afterDeleteSuccess(KeyParams keys, T result) {
		// default to nothing
	}

	protected void afterDeleteError(KeyParams keys, Throwable e) {
		// default to nothing
	}

	@GitRead
	public WrapperVO<Long> count(String filter, String paging, @GitCommit String commit, @GitCommit Long at) {
		try {
			beforeCount(filter, paging, commit, at);
			WrapperVO<Long> result = new WrapperVO<>(repository().count(readDirectory(), filter, paging));
			afterCountSuccess(filter, paging, commit, at, result);
			return result;
		} catch (Throwable e) {
			afterCountError(filter, paging, commit, at, e);
			throw e;
		}
	}

	protected void beforeCount(String filter, String paging, String commit, Long at) {
		// default to nothing
	}

	protected void afterCountSuccess(String filter, String paging, String commit, Long at, WrapperVO<Long> result) {
		// default to nothing
	}

	protected void afterCountError(String filter, String paging, String commit, Long at, Throwable e) {
		// default to nothing
	}

	@GitRead
	public List<T> list(String filter, String paging, String sorting, @GitCommit String commit, @GitCommit Long at) {
		try {
			beforeList(filter, paging, sorting, commit, at);
			List<T> result = repository().list(readDirectory(), filter, paging, sorting);
			afterListSuccess(filter, paging, sorting, commit, at, result);
			return result;
		} catch (Throwable e) {
			afterListError(filter, paging, sorting, commit, at, e);
			throw e;
		}
	}

	protected void beforeList(String filter, String paging, String sorting, String commit, Long at) {
		// default to nothing
	}

	protected void afterListSuccess(String filter, String paging, String sorting, String commit, Long at,
			List<T> result) {
		// default to nothing
	}

	protected void afterListError(String filter, String paging, String sorting, String commit, Long at, Throwable e) {
		// default to nothing
	}

	// +------------- PROPERTY METHODS ------------------+

	@GitWrite
	public Object newValue(String property, String data, Object reference) {
		return repository().newValue(property, data, reference);
	}

	@GitWrite
	public T setProperty(KeyParams keys, String property, String data) {
		try {
			beforeSetProperty(keys, property, data);
			T result = repository().setProperty(writeDirectory(), keys, property, data);
			afterSetPropertySuccess(keys, property, data, result);
			return result;
		} catch (Throwable e) {
			afterSetPropertyError(keys, property, data, e);
			throw e;
		}
	}

	protected void beforeSetProperty(KeyParams keys, String property, String data) {
		// default to nothing
	}

	protected void afterSetPropertySuccess(KeyParams keys, String property, String data, T result) {
		// default to nothing
	}

	protected void afterSetPropertyError(KeyParams keys, String property, String data, Throwable e) {
		// default to nothing
	}

	@GitWrite
	public List<T> setProperty(String property, String data, String filter, String paging, String sorting) {
		try {
			beforeSetProperty(property, data, filter, paging, sorting);
			List<T> result = repository().setProperty(writeDirectory(), property, data, filter, paging, sorting);
			afterSetPropertySuccess(property, data, filter, paging, sorting, result);
			return result;
		} catch (Throwable e) {
			afterSetPropertyError(property, data, filter, paging, sorting, e);
			throw e;
		}
	}

	protected void beforeSetProperty(String property, String data, String filter, String paging, String sorting) {
		// default to nothing
	}

	protected void afterSetPropertySuccess(String property, String data, String filter, String paging, String sorting,
			List<T> result) {
		// default to nothing
	}

	protected void afterSetPropertyError(String property, String data, String filter, String paging, String sorting,
			Throwable e) {
		// default to nothing
	}

	@GitRead
	public WrapperVO<Object> getProperty(KeyParams keys, String property, @GitCommit String commit,
			@GitCommit Long at) {
		try {
			beforeGetProperty(keys, property, commit, at);
			WrapperVO<Object> result = new WrapperVO<>(repository().getProperty(readDirectory(), keys, property));
			afterGetPropertySuccess(keys, property, commit, at, result);
			return result;
		} catch (Throwable e) {
			afterGetPropertyError(keys, property, commit, at, e);
			throw e;
		}
	}

	protected void beforeGetProperty(KeyParams keys, String property, String commit, Long at) {
		// default to nothing
	}

	protected void afterGetPropertySuccess(KeyParams keys, String property, String commit, Long at,
			WrapperVO<Object> result) {
		// default to nothing
	}

	protected void afterGetPropertyError(KeyParams keys, String property, String commit, Long at, Throwable e) {
		// default to nothing
	}

	@GitRead
	public Map<String, Object> properties(KeyParams keys, KeyParams names, @GitCommit String commit,
			@GitCommit Long at) {
		Map<String, Object> result;
		try {
			beforeProperties(keys, names, commit, at);
			result = repository().properties(readDirectory(), keys, names);
			afterPropertiesSuccess(keys, names, commit, at, result);
			return result;
		} catch (Throwable e) {
			afterPropertiesError(keys, names, commit, at, e);
			throw e;
		}
	}

	protected void beforeProperties(KeyParams keys, KeyParams names, String commit, Long at) {
		// default to nothing
	}

	protected void afterPropertiesSuccess(KeyParams keys, KeyParams names, String commit, Long at,
			Map<String, Object> result) {
		// default to nothing
	}

	protected void afterPropertiesError(KeyParams keys, KeyParams names, String commit, Long at, Throwable e) {
		// default to nothing
	}

	@GitRead
	public Map<String, Map<String, Object>> properties(KeyParams names, String filter, String paging, String sorting,
			@GitCommit String commit, @GitCommit Long at) {
		Map<String, Map<String, Object>> result;
		try {
			beforeProperties(names, filter, paging, sorting, commit, at);
			result = repository().properties(readDirectory(), names, filter, paging, sorting);
			afterPropertiesSuccess(names, filter, paging, sorting, commit, at, result);
			return result;
		} catch (Throwable e) {
			afterPropertiesError(names, filter, paging, sorting, commit, at, e);
			throw e;
		}
	}

	protected void beforeProperties(KeyParams names, String filter, String paging, String sorting, String commit,
			Long at) {
		// default to nothing
	}

	protected void afterPropertiesSuccess(KeyParams names, String filter, String paging, String sorting, String commit,
			Long at, Map<String, Map<String, Object>> result) {
		// default to nothing
	}

	protected void afterPropertiesError(KeyParams names, String filter, String paging, String sorting, String commit,
			Long at, Throwable e) {
		// default to nothing
	}

	// +------------- RESOURCE METHODS ------------------+

	@GitWrite
	public T setResource(KeyParams keys, Resource resource) {
		try {
			beforeSetResource(keys, resource);
			T result = repository().setResource(writeDirectory(), keys, resource);
			afterSetResourceSuccess(keys, resource, result);
			return result;
		} catch (Throwable e) {
			afterSetResourceError(keys, resource, e);
			throw e;
		}
	}

	protected void beforeSetResource(KeyParams keys, Resource resource) {
		if (repository().existsResources(writeDirectory(), keys, resource.getMetadata().getPath())) {
			throw new IllegalArgumentException("Resource already exists.");
		}
	}

	protected void afterSetResourceSuccess(KeyParams keys, Resource resource, T result) {
		// default to nothing
	}

	protected void afterSetResourceError(KeyParams keys, Resource resource, Throwable e) {
		// default to nothing
	}

	@GitRead
	public Resource getResource(KeyParams keys, String path, @GitCommit String commit, @GitCommit Long at) {
		try {
			beforeGetResource(keys, path, commit, at);
			Resource result = repository().getResource(readDirectory(), keys, path);
			afterGetResourceSuccess(keys, path, commit, at, result);
			return result;
		} catch (Throwable e) {
			afterGetResourceError(keys, path, commit, at, e);
			throw e;
		}
	}

	protected void beforeGetResource(KeyParams keys, String path, String commit, Long at) {
		// default to nothing
	}

	protected void afterGetResourceSuccess(KeyParams keys, String path, String commit, Long at, Resource result) {
		// default to nothing
	}

	protected void afterGetResourceError(KeyParams keys, String path, String commit, Long at, Throwable e) {
		// default to nothing
	}

	@GitWrite
	public T updateResource(KeyParams keys, Resource resource) {
		try {
			beforeUpdateResource(keys, resource);
			T result = repository().setResource(writeDirectory(), keys, resource);
			afterUpdateResourceSuccess(keys, resource, result);
			return result;
		} catch (Throwable e) {
			afterUpdateResourceError(keys, resource, e);
			throw e;
		}
	}

	protected void beforeUpdateResource(KeyParams keys, Resource resource) {
		if (!repository().existsResources(writeDirectory(), keys, resource.getMetadata().getPath())) {
			throw new IllegalArgumentException("Resource not found.");
		}
	}

	private void afterUpdateResourceSuccess(KeyParams keys, Resource resource, T result) {
		// default to nothing
	}

	private void afterUpdateResourceError(KeyParams keys, Resource resource, Throwable e) {
		// default to nothing
	}

	@GitWrite
	public T deleteResource(KeyParams keys, String path) {
		try {
			beforeDeleteResource(keys, path);
			T result = repository().deleteResource(writeDirectory(), keys, path);
			afterDeleteResourceSuccess(keys, path, result);
			return result;
		} catch (Throwable e) {
			afterDeleteResourceError(keys, path, e);
			throw e;
		}
	}

	protected void beforeDeleteResource(KeyParams keys, String path) {
		// default to nothing
	}

	protected void afterDeleteResourceSuccess(KeyParams keys, String path, T result) {
		// default to nothing
	}

	protected void afterDeleteResourceError(KeyParams keys, String path, Throwable e) {
		// default to nothing
	}

	@GitRead
	public WrapperVO<Long> countResources(KeyParams keys, String filter, String paging, @GitCommit String commit,
			@GitCommit Long at) {
		try {
			beforeCountResources(keys, filter, paging, commit, at);
			WrapperVO<Long> result = new WrapperVO<>(
					repository().countResources(readDirectory(), keys, filter, paging));
			afterCountResourcesSuccess(keys, filter, paging, commit, at, result);
			return result;
		} catch (Throwable e) {
			afterCountResourcesError(keys, filter, paging, commit, at, e);
			throw e;
		}
	}

	protected void beforeCountResources(KeyParams keys, String filter, String paging, String commit, Long at) {
		// default to nothing
	}

	protected void afterCountResourcesSuccess(KeyParams keys, String filter, String paging, String commit, Long at,
			WrapperVO<Long> result) {
		// default to nothing
	}

	protected void afterCountResourcesError(KeyParams keys, String filter, String paging, String commit, Long at,
			Throwable e) {
		// default to nothing
	}

	@GitRead
	public List<Resource> listResources(KeyParams keys, String filter, String paging, String sorting,
			@GitCommit String commit, @GitCommit Long at) {
		try {
			beforeListResources(keys, filter, paging, sorting, commit, at);
			List<Resource> result = repository().listResources(readDirectory(), keys, filter, paging, sorting);
			afterListResourcesSuccess(keys, filter, paging, sorting, commit, at, result);
			return result;
		} catch (Throwable e) {
			afterListResourcesError(keys, filter, paging, sorting, commit, at, e);
			throw e;
		}
	}

	protected void beforeListResources(KeyParams keys, String filter, String paging, String sorting, String commit,
			Long at) {
		// default to nothing
	}

	protected void afterListResourcesSuccess(KeyParams keys, String filter, String paging, String sorting,
			String commit, Long at, List<Resource> result) {
		// default to nothing
	}

	protected void afterListResourcesError(KeyParams keys, String filter, String paging, String sorting, String commit,
			Long at, Throwable e) {
		// default to nothing
	}

	// +------------- HISTORY METHODS ------------------+

	@GitRead
	public List<HistoryVO> history(KeyParams keys, String paging) {
		try {
			FilePaging page = repository().paging(paging);
			Integer skip = page != null ? page.getSkip() : null;
			Integer max = page != null ? page.getMax() : null;
			beforeHistory(keys, paging, skip, max);
			List<HistoryVO> result = mapper.mapList(//
					gits.history(group(), repository().location(readDirectory(), keys), skip, max), //
					HistoryVO.class);
			afterHistorySuccess(keys, paging, skip, max, result);
			return result;
		} catch (Throwable e) {
			afterHistoryError(keys, paging, e);
			throw e;
		}
	}

	protected void beforeHistory(KeyParams keys, String paging, Integer skip, Integer max) {
		// default to nothing
	}

	protected void afterHistorySuccess(KeyParams keys, String paging, Integer skip, Integer max,
			List<HistoryVO> result) {
		// default to nothing
	}

	protected void afterHistoryError(KeyParams keys, String paging, Throwable e) {
		// default to nothing
	}

	@GitRead
	public List<HistoryVO> historyResources(KeyParams keys, String path, String paging) {
		try {
			FilePaging page = repository().paging(paging);
			Integer skip = page != null ? page.getSkip() : null;
			Integer max = page != null ? page.getMax() : null;
			beforeHistoryResources(keys, path, paging, skip, max);
			List<HistoryVO> result = mapper.mapList(//
					gits.history(group(), repository().locationResources(readDirectory(), keys, path), skip, max), //
					HistoryVO.class);
			afterHistoryResourcesSuccess(keys, path, paging, skip, max, result);
			return result;
		} catch (Throwable e) {
			afterHistoryResourcesError(keys, path, paging, e);
			throw e;
		}
	}

	protected void beforeHistoryResources(KeyParams keys, String path, String paging, Integer skip, Integer max) {
		// default to nothing
	}

	protected void afterHistoryResourcesSuccess(KeyParams keys, String path, String paging, Integer skip, Integer max,
			List<HistoryVO> result) {
		// default to nothing
	}

	protected void afterHistoryResourcesError(KeyParams keys, String path, String paging, Throwable e) {
		// default to nothing
	}
}