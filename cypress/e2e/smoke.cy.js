describe('Smoke API', () => {
  it('should return empleados list with admin credentials', () => {
    const configuredBaseUrl = (Cypress.config('baseUrl') || '').replace(/\/$/, '');
    const baseUrl = /^https?:\/\/localhost$/i.test(configuredBaseUrl)
      ? 'http://localhost:8081'
      : (configuredBaseUrl || 'http://localhost:8081');

    cy.request({
      method: 'GET',
      url: `${baseUrl}/api/v1/empleados?page=0&size=1`,
      auth: {
        username: 'admin',
        password: 'admin123'
      }
    }).then((response) => {
      expect(response.status).to.eq(200);
      expect(response.body).to.have.property('data');
      expect(response.body).to.have.property('pagination');
    });
  });
});
