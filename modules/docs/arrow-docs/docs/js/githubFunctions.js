/**
 * Aux function to retrieve repository stars and watchers count info from
 * GitHub API and set it on its proper nodes.
 */
async function loadGitHubStats() {
    const ghAPI = `https://api.github.com/repos/arrow-kt/arrow`;
    const ghDataResponse = await fetch(ghAPI);
    const ghData = await ghDataResponse.json();
    // Meta stars
    const ghMetaAPI = `https://api.github.com/repos/arrow-kt/arrow-meta`;
    const ghMetaDataResponse = await fetch(ghMetaAPI);
    const ghMetaData = await ghMetaDataResponse.json();

    const starsElement = document.querySelector("#stars");
    starsElement.textContent = ghData.stargazers_count + ghMetaData.stargazers_count;
}

// Init call
function loadGithubEvent() {
  loadGitHubStats();
}


// Attach the functions to each event they are interested in
window.addEventListener("load", loadGithubEvent);
