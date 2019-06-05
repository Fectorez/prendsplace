import { NgModule } from '@angular/core';

import { Prendsplace2SharedLibsModule, JhiAlertComponent, JhiAlertErrorComponent } from './';

@NgModule({
  imports: [Prendsplace2SharedLibsModule],
  declarations: [JhiAlertComponent, JhiAlertErrorComponent],
  exports: [Prendsplace2SharedLibsModule, JhiAlertComponent, JhiAlertErrorComponent]
})
export class Prendsplace2SharedCommonModule {}
