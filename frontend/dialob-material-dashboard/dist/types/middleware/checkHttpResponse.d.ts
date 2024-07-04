export declare const checkHttpResponse: (response: Response, setLoginRequired: () => any) => Response | Promise<never>;
export declare const handleRejection: (ex: any, setTechnicalError: any) => void;
export declare const checkSearchHttpResponse: (response: {
    ok: any;
    status: number;
    statusText: string | undefined;
}, setLoginRequired: () => any) => {
    ok: any;
    status: number;
    statusText: string | undefined;
} | Promise<never>;
