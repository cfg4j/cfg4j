/*
 * Copyright 2015 Norbert Potocki (norbert.potocki@nort.pl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package pl.nort.config.source;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.data.MapEntry;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

public class GitConfigurationSourceIntegrationTest {

  @Rule
  public ExpectedException expectedException = ExpectedException.none();

  private SampleGitRepo remoteRepo;

  @Before
  public void setUp() throws Exception {
    remoteRepo = new SampleGitRepo();
    remoteRepo.changeProperty("application.properties", "some.setting", "value");
  }

  @After
  public void tearDown() throws Exception {
    remoteRepo.remove();
  }

  @Test
  public void shouldThrowWhenUnableToCreateLocalCloneOnNoTempDir() throws Exception {
    expectedException.expect(GitConfigurationSourceException.class);
    new GitConfigurationSource(remoteRepo.getURI(), "/someNonexistentDir/lkfjalfcz", "existing-path");
  }

  @Test
  public void shouldThrowOnInvalidRemote() throws Exception {
    expectedException.expect(GitConfigurationSourceException.class);
    new GitConfigurationSource(File.createTempFile("temp", "temp").getAbsolutePath());
  }

  @Test
  public void shouldReadConfigFromRemoteRepository() throws Exception {
    try (GitConfigurationSource gitConfigurationSource = new GitConfigurationSource(remoteRepo.getURI())) {
      assertThat(gitConfigurationSource.getConfiguration()).contains(MapEntry.entry("some.setting", "value"));
    }
  }

  @Test
  public void refreshShouldSyncWithRepoState() throws Exception {
    try (GitConfigurationSource gitConfigurationSource = new GitConfigurationSource(remoteRepo.getURI())) {
      remoteRepo.changeProperty("application.properties", "some.setting", "otherValue");
      gitConfigurationSource.refresh();

      assertThat(gitConfigurationSource.getConfiguration()).contains(MapEntry.entry("some.setting", "otherValue"));
    }
  }

  @Test
  public void refreshShouldThrowOnSyncProblems() throws Exception {
    try (GitConfigurationSource gitConfigurationSource = new GitConfigurationSource(remoteRepo.getURI())) {
      remoteRepo.remove();

      expectedException.expect(IllegalStateException.class);
      gitConfigurationSource.refresh();
    }
  }

  private static class SampleGitRepo {

    private final Git repo;

    public SampleGitRepo() throws IOException, GitAPIException {
      File tempFile = File.createTempFile("test-repo.git", "");
      repo = createLocalRepo(tempFile);
    }

    public String getURI() {
      return repo.getRepository().getWorkTree().getAbsolutePath();
    }

    public void changeProperty(String propFilePath, String key, String value) throws IOException, GitAPIException {
      writePropertyToFile(propFilePath, key, value);
      commitChangesTo(repo);
    }

    public void remove() {
      repo.close();
      deleteDir(new File(getURI()));
    }

    private Git createLocalRepo(File path) throws IOException, GitAPIException {
      path.delete();
      return Git.init()
          .setDirectory(path)
          .call();
    }

    private void writePropertyToFile(String propFilePath, String key, String value) throws IOException {
      OutputStream out = new FileOutputStream(getURI() + "/" + propFilePath);
      Properties properties = new Properties();
      properties.put(key, value);
      properties.store(out, "");
      out.close();
    }

    private void commitChangesTo(Git repo) throws GitAPIException {
      repo.add()
          .addFilepattern(".")
          .call();

      repo.commit()
          .setMessage("config change")
          .call();
    }

    public void deleteDir(File directory) {
      if (directory.exists()) {
        File[] files = directory.listFiles();

        if (files != null) {
          for (File file : files) {
            if (file.isDirectory()) {
              deleteDir(file);
            } else {
              file.delete();
            }
          }
        }
        directory.delete();
      }
    }
  }

}