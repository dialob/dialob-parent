const DEBUG_FORM = {
  _id: "e8fa5f95873b08b1013a5f1fdd16be41",
  _rev: "5-044c0e3931d84d4a67de422da46aa57a",
  data: {
    questionnaire: {
      id: "questionnaire",
      type: "questionnaire",
      label: { en: "New Dialog" },
      items: ["page1"]
    },
    page1: {
      id: "page1",
      type: "group",
      label: { en: "New Page" },
      items: ["group1"]
    },
    group1: {
      id: "group1",
      type: "group",
      label: { en: "New Group" },
      props: {
        columns: 1,
        something: 'something'
      }
    }
  },
  metadata: {
    label: "Simple",
    created: "2018-04-10T08:58:40.585+0000",
    lastSaved: "2018-04-10T08:58:56.438+0000",
    valid: true,
    creator:
      'Name: [3caa74e7-0182-4afa-aadb-8b968984d768], Granted Authorities: [ROLE_USER], User Attributes: [sub=3caa74e7-0182-4afa-aadb-8b968984d768, previous_logon_time=1523016863626, user_name=villu.vaimets@resys.io, origin=microsoft-office365, amr=["ext"], iss=https://login.resys.io/oauth/token, client_id=itest.dialob.io, acr={"values":["urn:oasis:names:tc:SAML:2.0:ac:classes:unspecified"]}, zid=uaa, grant_type=authorization_code, azp=itest.dialob.io, scope=["openid"], auth_time=1523350705, exp=Tue Apr 10 20:58:25 UTC 2018, iat=Tue Apr 10 08:58:25 UTC 2018, email=villu.vaimets@resys.io, jti=c1cacc5e628642fab398ce0a0d6ade3c, given_name=Villu, authorities=[itest, ff.admin, openid, profile, questionnaires, forms], aud=[itest.dialob.io], user_id=3caa74e7-0182-4afa-aadb-8b968984d768, name=Villu Vaimets, phone_number=null, family_name=Vaimets, rev_sig=4aaac29d, cid=itest.dialob.io]',
    tenantId: "itest",
    savedBy:
      'Name: [3caa74e7-0182-4afa-aadb-8b968984d768], Granted Authorities: [ROLE_USER], User Attributes: [sub=3caa74e7-0182-4afa-aadb-8b968984d768, previous_logon_time=1523016863626, user_name=villu.vaimets@resys.io, origin=microsoft-office365, amr=["ext"], iss=https://login.resys.io/oauth/token, client_id=itest.dialob.io, acr={"values":["urn:oasis:names:tc:SAML:2.0:ac:classes:unspecified"]}, zid=uaa, grant_type=authorization_code, azp=itest.dialob.io, scope=["openid"], auth_time=1523350705, exp=Tue Apr 10 20:58:25 UTC 2018, iat=Tue Apr 10 08:58:25 UTC 2018, email=villu.vaimets@resys.io, jti=c1cacc5e628642fab398ce0a0d6ade3c, given_name=Villu, authorities=[itest, ff.admin, openid, profile, questionnaires, forms], aud=[itest.dialob.io], user_id=3caa74e7-0182-4afa-aadb-8b968984d768, name=Villu Vaimets, phone_number=null, family_name=Vaimets, rev_sig=4aaac29d, cid=itest.dialob.io]',
    languages: ["en"]
  }
};

export default DEBUG_FORM;
