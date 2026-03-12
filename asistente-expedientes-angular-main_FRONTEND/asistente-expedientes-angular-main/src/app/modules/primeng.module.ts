import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import {CardModule} from 'primeng/card';
import {InputMaskModule} from 'primeng/inputmask';
import {CheckboxModule} from 'primeng/checkbox';
import {ToggleButtonModule} from 'primeng/togglebutton';
import {RadioButtonModule} from 'primeng/radiobutton';
import {InputTextModule} from 'primeng/inputtext';
import {DialogModule} from 'primeng/dialog';
import {PaginatorModule} from 'primeng/paginator';
import {ProgressBarModule} from 'primeng/progressbar';
import {ProgressSpinnerModule} from 'primeng/progressspinner';
import {MessagesModule} from 'primeng/messages';
import {MessageModule} from 'primeng/message';
import {KnobModule} from 'primeng/knob';
import {ToastModule} from 'primeng/toast';

@NgModule({
  declarations: [],
  imports: [
    CommonModule
  ],
  exports: [
    CardModule,
    InputMaskModule,
    CheckboxModule,
    ToggleButtonModule,
    RadioButtonModule,
    InputTextModule,
    DialogModule,
    PaginatorModule,
    ProgressBarModule,
    ProgressSpinnerModule,
    MessageModule,
    MessagesModule,
    KnobModule,
    ToastModule
  ]
})
export class PrimengModule { }
