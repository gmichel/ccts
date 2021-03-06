USE [PSCI_CCTS]
GO
/****** Object:  StoredProcedure [svm].[SP_StoreModel]    Script Date: 9/24/2018 9:24:26 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

-- =============================================
-- Author:		gjm
-- =============================================

CREATE PROCEDURE [svm].[SP_StoreModel]
	@ModelOrgan varchar(50),
	@ModelValue  varchar(max),
	@ModelStation int,
	@ModelDateCreated datetime,
	@ModelDateUpdated datetime
	AS
BEGIN
	SET NOCOUNT ON;
   UPDATE svm.Model SET ModelValue = @ModelValue , ModelDateUpdated = @ModelDateUpdated
    where ModelOrgan = @ModelOrgan and ModelStation = @ModelStation
	IF @@ROWCOUNT = 0
	BEGIN   
	insert into svm.Model  values (@ModelOrgan,@ModelValue,@ModelStation,@ModelDateCreated,@ModelDateUpdated )
    END 
END
GO
