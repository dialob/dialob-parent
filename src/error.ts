class DialobRequestError extends Error {
  reason: string;
  code: number;
  constructor(reason: string, code: number) {
    super(reason);

    Object.setPrototypeOf(this, DialobRequestError.prototype);
    this.reason = reason;
    this.code = code;
  }
}

class DialobError extends Error {
  constructor(reason: string) {
    super(reason);
    Object.setPrototypeOf(this, DialobError.prototype);
  }
}
