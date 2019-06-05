import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { Prendsplace2SharedLibsModule, Prendsplace2SharedCommonModule, JhiLoginModalComponent, HasAnyAuthorityDirective } from './';

@NgModule({
  imports: [Prendsplace2SharedLibsModule, Prendsplace2SharedCommonModule],
  declarations: [JhiLoginModalComponent, HasAnyAuthorityDirective],
  entryComponents: [JhiLoginModalComponent],
  exports: [Prendsplace2SharedCommonModule, JhiLoginModalComponent, HasAnyAuthorityDirective],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class Prendsplace2SharedModule {
  static forRoot() {
    return {
      ngModule: Prendsplace2SharedModule
    };
  }
}
