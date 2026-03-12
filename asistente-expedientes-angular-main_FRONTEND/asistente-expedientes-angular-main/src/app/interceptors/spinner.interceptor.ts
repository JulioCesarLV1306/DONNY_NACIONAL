import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { SpinnerService } from "../services/spinner.service";
import { finalize } from 'rxjs/operators';
import { delay } from 'rxjs/operators';

@Injectable()
export class SpinnerInterceptor implements HttpInterceptor {
    constructor(private spinnerService: SpinnerService) { }

    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        if (req.method != 'PUT') this.spinnerService.show();
        return next.handle(req).pipe(
            finalize(() => {
                if (req.method != 'PUT') this.spinnerService.hide()
            }
            )
        )
    }
}


