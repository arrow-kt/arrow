# Repositories: How to move content from one repository to another one

## Goal

The content from the source repository should keep the Git history.

## Steps

1. Create a new repository with the content to move:
```
git clone git@github.com:arrow-kt/<source-repository>.git
cd <source-repository>
git-filter-repo --path <path-to-extract> \
 --path <path-to-extract> \
 --path <path-to-extract> \
 ...
```
2. Publish that new repository
3. Add it as a remote repository to the target repository:
```
git clone git@github.com:arrow-kt/<target-repository>.git
cd <target-repository>
git remote add new-content <url>
git fetch new-content
```
4. Merge the extracted content:
```
cd <target-repository>
git merge --allow-unrelated-histories new-content/master
# (adapt the content if it's necessary)
git push origin master
```
5. Remove the published repository (step 2)
6. Create a pull request into the source repository to remove the extracted content
