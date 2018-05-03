
function checkResponse(response) {
  if (response.ok) {
    return response;
  } else {
    Promise.reject(new Error(response.statusText));
  }
}

export default class FormService {
  constructor(baseUrl, csrf) {
    this.baseUrl = baseUrl;
    this.csrf = csrf && csrf.toJS();
  }

  doFetch(url, method, body = null) {
    let headers = {
      'Accept': 'application/json',
      'Content-Type': 'application/json; charset=UTF-8'
    };
    if (this.csrf) {
      headers[this.csrf.headerName] = this.csrf.token;
    }
    let options = {
      method,
      credentials: 'include',
      headers,
      body: body && JSON.stringify(body)
    };
    return fetch(url, options)
      .then(response => checkResponse(response))
      .then(response => response.json());
  }

  loadForm(formId) {
    return this.doFetch(this.baseUrl + '/forms/' + formId, 'get');
  }

  saveForm(formData) {
    return this.doFetch(this.baseUrl + '/forms/' + formData._id, 'post', formData);
  }

  duplicateItem(formData, itemId) {
    return this.doFetch(this.baseUrl + '/forms/actions/itemCopy?itemId=' + itemId, 'post', formData);
  }

  changeItemId(formData, oldId, newId) {
    return this.doFetch(`${this.baseUrl}/forms/${formData._id}?oldId=${oldId}&newId=${newId}`, 'post', formData);
  }

}