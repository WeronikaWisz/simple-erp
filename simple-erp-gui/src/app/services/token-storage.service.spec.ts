import { TestBed } from '@angular/core/testing';

import { TokenStorageService } from './token-storage.service';

describe('TokenStorageService', () => {
  let service: TokenStorageService;

  let testUser = {
    username: "username"
  }

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TokenStorageService);

    let store : { [key:string]:any; } = {};
    const mockSessionStorage = {
      getItem: (key: string): string => {
        return key in store ? store[key] : null;
      },
      setItem: (key: string, value: string) => {
        store[key] = `${value}`;
      },
      removeItem: (key: string) => {
        delete store[key];
      },
      clear: () => {
        store = {};
      }
    };
    spyOn(sessionStorage, 'getItem')
      .and.callFake(mockSessionStorage.getItem);
    spyOn(sessionStorage, 'setItem')
      .and.callFake(mockSessionStorage.setItem);
    spyOn(sessionStorage, 'removeItem')
      .and.callFake(mockSessionStorage.removeItem);
    spyOn(sessionStorage, 'clear')
      .and.callFake(mockSessionStorage.clear);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('saveToken should set token in sessionStorage', () => {
    service.saveToken('sometoken');
    expect(sessionStorage.getItem('auth-token')).toEqual('sometoken');
  });

  it('getToken should return stored token from sessionStorage', () => {
    sessionStorage.setItem('auth-token', 'anothertoken');
    expect(service.getToken()).toEqual('anothertoken');
  });

  it('saveUser should set user in sessionStorage', () => {
    service.saveUser(testUser);
    expect(sessionStorage.getItem('auth-user')).toEqual(JSON.stringify(testUser));
  });

  it('getUser should return stored user from sessionStorage', () => {
    sessionStorage.setItem('auth-user', JSON.stringify(testUser));
    expect(service.getUser()).toEqual(testUser);
  });

  it('getUser should return empty object if no user from sessionStorage', () => {
    expect(service.getUser()).toEqual({});
  });

  it('signOut should clear sessionStorage', () => {
    service.signOut()
    expect(sessionStorage.clear).toHaveBeenCalled()
  });
});
