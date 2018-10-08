package org.opensourcebim.modelsetanalyzer;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.bimserver.interfaces.objects.SProject;
import org.bimserver.plugins.services.BimServerClientInterface;
import org.bimserver.shared.exceptions.BimServerClientException;
import org.bimserver.shared.exceptions.PublicInterfaceNotFoundException;
import org.bimserver.shared.exceptions.ServerException;
import org.bimserver.shared.exceptions.UserException;

public class ModelSetAnalyzer {

	private BimServerClientInterface client;
	private final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(64, 64, 12, TimeUnit.HOURS, new ArrayBlockingQueue<>(1000));
	private AnalyzedModelSet analyzedModelSet = new AnalyzedModelSet();
	private int revisionIdCounter = 1;
	private long start;

	public ModelSetAnalyzer(BimServerClientInterface client) {
		this.client = client;
		start = System.nanoTime();
	}

	public void addRevision(SProject project, long roid) throws UserException, ServerException, PublicInterfaceNotFoundException, BimServerClientException {
		Task task = new Task(analyzedModelSet, client, project, roid, revisionIdCounter++);
		threadPoolExecutor.submit(task);
	}

	public void awaitTermination() {
		try {
			threadPoolExecutor.shutdown();
			threadPoolExecutor.awaitTermination(8, TimeUnit.HOURS);
			long end = System.nanoTime();
			System.out.println(((end - start) / 1000000) + " ms");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public AnalyzedModelSet getResults() {
		return analyzedModelSet;
	}
}
