describe('Test sign in', () => {
  it('should redirect to login page', () => {
    cy.visit('/')
    cy.url().should('includes', 'login')
  })
  it('should have disabled login button', () => {
    cy.get('#submit-login-button')
      .should('be.disabled')
  })
  it('should login', () => {
    cy.fixture('login-data').then((data) => {
      const {username, password} = data
      cy.get('input[formcontrolname="username"]').type(username)
      cy.get('input[formcontrolname="password"]').type(password)
      cy.get('#submit-login-button')
        .should('not.be.disabled')
      cy.get('#submit-login-button').click()
      cy.url().should('not.include', 'login')
    })
  })
  it('should save token in sessionStorage', () => {
    cy.window().its("sessionStorage")
      .invoke("getItem", "auth-token")
      .should("exist")
  })
})
describe('Test change default task user', () => {
  it('should have enable delegate change default user button', () => {
    cy.visit('/default-users')
    cy.get('#change-default-user')
      .should('not.be.disabled')
    cy.get('#change-default-user').click()
  })
  it('should have disable save changed user button', () => {
    cy.get('#save-default-user')
      .should('be.disabled')
  })
  it('should save default user', () => {
    cy.get('#select-default-user').click();
    cy.get('#option-default-user').first().click()
    cy.get('#save-default-user')
      .should('not.be.disabled')
  })
})
describe('Test suggest stock quantity', () => {
  it('should have enable delegate purchase button', () => {
    cy.visit('/browse-supplies')
    cy.get('#purchase-button')
      .should('not.be.disabled')
    cy.get('#purchase-button').click()
  })
  it('should have disable suggest purchase button', () => {
    cy.get('#suggest-button')
      .should('be.disabled')
  })
  it('should suggest purchase', () => {
    cy.fixture('suggest-purchase-data').then((data) => {
      const { days } = data
      cy.get('input[formcontrolname="days"]').clear()
      cy.get('input[formcontrolname="days"]').type(days)
      cy.get('#suggest-button')
        .should('not.be.disabled')
      cy.get('#suggest-button').click()
      cy.get('input[formcontrolname="quantity"]').should('not.have.value', '')
    })
  })
})
describe('Test sign out', () => {
  it('should have enabled profile button', () => {
    cy.visit('/profile')
    cy.get('#profile-button')
      .should('not.be.disabled')
    cy.get('#profile-button').click()
  })
  it('should have logout button', () => {
    cy.get('#logout-button')
      .should('exist')
    cy.get('#logout-button').click()
  })
  it('should redirect to login page', () => {
    cy.url().should('includes', 'login')
  })
})
describe('Test sign in user not exists', () => {
  it('should redirect to login page', () => {
    cy.visit('/')
    cy.url().should('includes', 'login')
  })
  it('should have disabled login button', () => {
    cy.get('#submit-login-button')
      .should('be.disabled')
  })
  it('should not login', () => {
    cy.fixture('login-data-error').then((data) => {
      const {username, password} = data
      cy.get('input[formcontrolname="username"]').type(username)
      cy.get('input[formcontrolname="password"]').type(password)
      cy.get('#submit-login-button')
        .should('not.be.disabled')
      cy.get('#submit-login-button').click()
      cy.url().should('include', 'login')
    })
  })
})
