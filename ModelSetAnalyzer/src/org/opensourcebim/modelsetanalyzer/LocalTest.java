package org.opensourcebim.modelsetanalyzer;

import java.nio.file.Paths;
import java.util.List;

import org.bimserver.client.json.JsonBimServerClientFactory;
import org.bimserver.interfaces.objects.SProject;
import org.bimserver.plugins.services.BimServerClientInterface;
import org.bimserver.shared.BimServerClientFactory;
import org.bimserver.shared.ChannelConnectionException;
import org.bimserver.shared.UsernamePasswordAuthenticationInfo;
import org.bimserver.shared.exceptions.BimServerClientException;
import org.bimserver.shared.exceptions.ServiceException;

public class LocalTest {
	public static void main(String[] args) {
		try (BimServerClientFactory factory = new JsonBimServerClientFactory("http://localhost:8080")) {
			try (BimServerClientInterface client = factory.create(new UsernamePasswordAuthenticationInfo("admin@bimserver.org", "admin"))) {
				ModelSetAnalyzer modelSetAnalyzer = new ModelSetAnalyzer(client);
				List<SProject> projects = client.getServiceInterface().getProjectsByName("IFC4");
				for (SProject project : projects) {
					long roid = project.getLastRevisionId();
					if (roid != -1) {
						modelSetAnalyzer.addRevision(project, roid);
					}
				}
				modelSetAnalyzer.awaitTermination();
				AnalyzedModelSet results = modelSetAnalyzer.getResults();
				results.toExcel(Paths.get("ifc4.xlsx"));
			}
		} catch (BimServerClientException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		} catch (ChannelConnectionException e) {
			e.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
}
