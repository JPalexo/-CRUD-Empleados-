describe('Login API', () => {
  const resolveBaseUrl = () => {
    const configuredBaseUrl = (Cypress.config('baseUrl') || '').replace(/\/$/, '');

    if (/^https?:\/\/localhost$/i.test(configuredBaseUrl)) {
      return 'http://localhost:8081';
    }

    return configuredBaseUrl || 'http://localhost:8081';
  };

  const createEmpleadoWithCredentials = (baseUrl) => {
    const uniqueSuffix = `${Date.now()}-${Cypress._.random(1000, 9999)}`;
    const password = `abc12345${Cypress._.random(10, 99)}`;
    const email = `empleado.cypress.${uniqueSuffix}@empresa.com`;

    return cy.request({
      method: 'POST',
      url: `${baseUrl}/api/v1/departamentos`,
      auth: {
        username: 'admin',
        password: 'admin123'
      },
      body: {
        nombre: `Dept Cypress ${uniqueSuffix}`
      }
    }).then((departamentoResponse) => {
      expect(departamentoResponse.status).to.eq(201);
      expect(departamentoResponse.body).to.have.property('clave');

      return cy.request({
        method: 'POST',
        url: `${baseUrl}/api/v1/empleados`,
        auth: {
          username: 'admin',
          password: 'admin123'
        },
        body: {
          nombre: `Empleado ${uniqueSuffix}`,
          direccion: `Direccion ${uniqueSuffix}`,
          telefono: `555-${Cypress._.random(1000, 9999)}`,
          email,
          password,
          departamentoClave: departamentoResponse.body.clave
        }
      }).then((empleadoResponse) => {
        expect(empleadoResponse.status).to.eq(201);

        return {
          email,
          password
        };
      });
    });
  };

  it('should authenticate a created employee in public login endpoint', () => {
    const baseUrl = resolveBaseUrl();

    createEmpleadoWithCredentials(baseUrl).then(({ email, password }) => {
      cy.request({
        method: 'POST',
        url: `${baseUrl}/api/v1/empleados/login`,
        body: {
          email,
          password
        }
      }).then((response) => {
        expect(response.status).to.eq(200);
        expect(response.body).to.have.property('authenticated', true);
        expect(response.body).to.have.nested.property('empleado.email', email);
        expect(response.body).to.have.nested.property('empleado.clave').match(/^EMP-[1-9][0-9]*$/);
      });
    });
  });

  it('should return unauthorized for invalid employee credentials', () => {
    const baseUrl = resolveBaseUrl();

    createEmpleadoWithCredentials(baseUrl).then(({ email }) => {
      cy.request({
        method: 'POST',
        url: `${baseUrl}/api/v1/empleados/login`,
        failOnStatusCode: false,
        body: {
          email,
          password: 'wrong123'
        }
      }).then((response) => {
        expect(response.status).to.eq(401);
        expect(response.body).to.have.property('message');
        expect(response.body.message).to.contain('Employee authentication failed');
      });
    });
  });
});
