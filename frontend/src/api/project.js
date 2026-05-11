import http from './http';
export function listProjectsApi() {
    return http.get('/projects');
}
export function getProjectBoardApi(projectId) {
    return http.get(`/projects/${projectId}/board`);
}
