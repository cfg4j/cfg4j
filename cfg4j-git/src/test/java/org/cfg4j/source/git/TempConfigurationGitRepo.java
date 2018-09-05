/*
 * Copyright 2015-2018 Norbert Potocki (norbert.potocki@nort.pl)
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
package org.cfg4j.source.git;

import org.cfg4j.source.files.TempConfigurationFileRepo;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Temporary local git repository that contains configuration files.
 */
class TempConfigurationGitRepo extends TempConfigurationFileRepo {

  private final Git repo;

  /**
   * Create temporary, local git repository. When you're done using it remove it by invoking {@link #remove()}
   * method.
   *
   * @throws IOException     when unable to create local directories
   * @throws GitAPIException when unable to execute git operations
   */
  TempConfigurationGitRepo(String dirName) throws IOException, GitAPIException {
    super(dirName);
    repo = createLocalRepo(dirPath);
  }

  /**
   * Change active branch to {@code branch}. Create the branch if it doesn't exist.
   *
   * @param branch branch to activate
   * @throws GitAPIException when unable to change branch.
   */
  void changeBranchTo(String branch) throws GitAPIException {
    boolean createBranch = true;

    List<Ref> refList = repo.branchList().call();
    if (anyRefMatches(refList, branch)) {
      createBranch = false;
    }

    repo.checkout()
        .setCreateBranch(createBranch)
        .setName(branch)
        .call();
  }

  /**
   * Change the {@code key} property to {@code value} and store it in a {@code propFilePath} properties file. Commits
   * the change.
   *
   * @param propFilePath relative path to the properties file in this repository
   * @param key          property key
   * @param value        property value
   * @throws IOException     when unable to modify properties file
   * @throws GitAPIException when unable to commit changes
   */
  @Override
  public void changeProperty(Path propFilePath, String key, String value) throws IOException {
    super.changeProperty(propFilePath, key, value);
    try {
      commitChanges();
    } catch (GitAPIException e) {
      throw new IOException(e);
    }
  }

  /**
   * Delete file from this repository. Commits changes.
   *
   * @param filePath relative file path to delete
   * @throws GitAPIException when unable to commit changes
   */
  @Override
  public void deleteFile(Path filePath) throws IOException {
    try {
      super.deleteFile(filePath);

      repo.rm()
          .addFilepattern(filePath.toString())
          .call();

      commitChanges();

    } catch (GitAPIException e) {
      throw new IOException(e);
    }
  }

  /**
   * Remove this repository. Silently fails if repo already removed.
   */
  @Override
  public void remove() {
    try {
      repo.close();
      super.remove();
    } catch (IOException e) {
      // NOP
    }
  }

  private Git createLocalRepo(Path path) throws IOException, GitAPIException {
    Files.delete(path);

    return Git.init()
        .setDirectory(path.toFile())
        .call();
  }

  private void commitChanges() throws GitAPIException {
    repo.add()
        .addFilepattern(".")
        .call();

    repo.commit()
        .setMessage("config change")
        .call();
  }

  private boolean anyRefMatches(List<Ref> refList, String branch) {
    for (Ref ref : refList) {
      if (ref.getName().replace("refs/heads/", "").equals(branch)) {
        return true;
      }
    }

    return false;
  }
}
