import { entityItemSelector } from '../../support/commands';
import {
  entityTableSelector,
  entityDetailsButtonSelector,
  entityDetailsBackButtonSelector,
  entityCreateButtonSelector,
  entityCreateSaveButtonSelector,
  entityCreateCancelButtonSelector,
  entityEditButtonSelector,
  entityDeleteButtonSelector,
  entityConfirmDeleteButtonSelector,
} from '../../support/entity';

describe('Boat e2e test', () => {
  const boatPageUrl = '/boat';
  const boatPageUrlPattern = new RegExp('/boat(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const boatSample = { name: 'efficient', description: 'Mosambik Implemented' };

  let boat: any;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/boats+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/boats').as('postEntityRequest');
    cy.intercept('DELETE', '/api/boats/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (boat) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/boats/${boat.id}`,
      }).then(() => {
        boat = undefined;
      });
    }
  });

  it('Boats menu should load Boats page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('boat');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response!.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Boat').should('exist');
    cy.url().should('match', boatPageUrlPattern);
  });

  describe('Boat page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(boatPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Boat page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/boat/new$'));
        cy.getEntityCreateUpdateHeading('Boat');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', boatPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/boats',
          body: boatSample,
        }).then(({ body }) => {
          boat = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/boats+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/boats?page=0&size=20>; rel="last",<http://localhost/api/boats?page=0&size=20>; rel="first"',
              },
              body: [boat],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(boatPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Boat page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('boat');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', boatPageUrlPattern);
      });

      it('edit button click should load edit Boat page', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Boat');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', boatPageUrlPattern);
      });

      it('last delete button click should delete instance of Boat', () => {
        cy.intercept('GET', '/api/boats/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('boat').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response!.statusCode).to.equal(200);
        });
        cy.url().should('match', boatPageUrlPattern);

        boat = undefined;
      });
    });
  });

  describe('new Boat page', () => {
    beforeEach(() => {
      cy.visit(`${boatPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Boat');
    });

    it('should create an instance of Boat', () => {
      cy.get(`[data-cy="name"]`).type('Integrated Buckinghamshire Frozen').should('have.value', 'Integrated Buckinghamshire Frozen');

      cy.get(`[data-cy="description"]`).type('scale Frozen Refined').should('have.value', 'scale Frozen Refined');

      cy.setFieldImageAsBytesOfEntity('pic', 'integration-test.png', 'image/png');

      // since cypress clicks submit too fast before the blob fields are validated
      cy.wait(200); // eslint-disable-line cypress/no-unnecessary-waiting
      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(201);
        boat = response!.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response!.statusCode).to.equal(200);
      });
      cy.url().should('match', boatPageUrlPattern);
    });
  });
});
